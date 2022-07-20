package com.merimdigitalmedia.underflow.handlers.flows;

import com.merimdigitalmedia.underflow.annotation.security.Secured;
import com.merimdigitalmedia.underflow.handlers.context.ContextHandler;
import com.merimdigitalmedia.underflow.mdc.MDCContext;
import com.merimdigitalmedia.underflow.mdc.MDCInterceptor;
import com.merimdigitalmedia.underflow.results.Result;
import com.merimdigitalmedia.underflow.results.http.HttpResult;
import com.merimdigitalmedia.underflow.results.http.SenderHttpResult;
import com.merimdigitalmedia.underflow.results.http.SenderResults;
import com.merimdigitalmedia.underflow.results.http.StandardResults;
import com.merimdigitalmedia.underflow.security.FlowSecurity;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * V2.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class FlowHandler implements HttpHandler, MDCContext, SenderResults, StandardResults {

    /**
     * The Logger.
     */
    final protected Logger logger;

    /**
     * The Flow security.
     */
    private final FlowSecurity<?, ?> flowSecurity;

    /**
     * Instantiates a new Flow handler.
     */
    public FlowHandler() {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.flowSecurity = null;
    }

    /**
     * Instantiates a new Flow handler.
     */
    public FlowHandler(final FlowSecurity<?, ?> flowSecurity) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.flowSecurity = flowSecurity;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) {
        MDCInterceptor.getInstance().accept(exchange);
        final ContextHandler context = new ContextHandler(this, exchange);

        if (context.isValid()) {
            final Optional<Secured> secured = context.requireSecurity();
            context.addInjectable(this.flowSecurity);
            if (this.flowSecurity != null && secured.isPresent()) {
                final Optional<?> optionalUser = this.flowSecurity.isLogged(exchange);
                if (optionalUser.isPresent()) {
                    if (this.flowSecurity.isAccessibleUnsafe(optionalUser.get(), context.getMethod())) {
                        // OK continue !
                        context.addInjectable(this.flowSecurity);
                        context.addInjectableUnsafe(this.flowSecurity.userRepresentationClass(), optionalUser.get());
                    } else {
                        // User is logged but doesn't have the right permissions.
                        this.onForbidden(exchange).process(exchange);
                        return;
                    }
                } else {
                    // User is not logged.
                    if (!secured.get().optional()) {
                        // User was required.
                        this.onUnauthorized(exchange).process(exchange);
                        return;
                    }
                }
            }
            context.execute();
        } else {
            final Result result = this.onNotFound(exchange);
            result.process(exchange);
        }
    }

    @Override
    public HttpResult result(final int code, final Consumer<Sender> exchangeData) {
        return new SenderHttpResult(code, exchangeData);
    }

    /**
     * On not found result.
     *
     * @param exchange the exchange
     * @return the result
     */
    public Result onNotFound(final HttpServerExchange exchange) {
        return this.notFound("Not Found");
    }

    /**
     * On not found result.
     *
     * @param exchange the exchange
     * @return the result
     */
    public Result onUnauthorized(final HttpServerExchange exchange) {
        return this.unauthorized("Unauthorized");
    }

    /**
     * On not found result.
     *
     * @param exchange the exchange
     * @return the result
     */
    public Result onForbidden(final HttpServerExchange exchange) {
        return this.forbidden("Forbidden");
    }

    /**
     * On exception result.
     *
     * @param exchange  the exchange
     * @param exception the exception
     * @return the result
     */
    public Result onException(final HttpServerExchange exchange, final Throwable exception) {
        return this.internalServerError("Internal Server Error");
    }
}
