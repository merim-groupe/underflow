package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.annotation.security.Secured;
import com.merim.digitalpayment.underflow.handlers.context.ContextHandler;
import com.merim.digitalpayment.underflow.mdc.MDCContext;
import com.merim.digitalpayment.underflow.mdc.MDCInterceptor;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.results.http.HttpResult;
import com.merim.digitalpayment.underflow.results.http.SenderHttpResult;
import com.merim.digitalpayment.underflow.results.http.SenderResults;
import com.merim.digitalpayment.underflow.results.http.StandardResults;
import com.merim.digitalpayment.underflow.security.FlowSecurity;
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
     *
     * @param flowSecurity the flow security
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
            if (this.flowSecurity != null) {
                final Optional<?> optionalUser = this.flowSecurity.isLogged(exchange);
                optionalUser.ifPresent(user -> context.addInjectableUnsafe(this.flowSecurity.userRepresentationClass(), user));

                if (secured.isPresent()) {
                    if (optionalUser.isPresent()) {
                        if (this.flowSecurity.isAccessibleUnsafe(optionalUser.get(), context.getMethod())) {
                            // OK continue !
                            context.addInjectableUnsafe(this.flowSecurity.userRepresentationClass(), optionalUser.get());
                        } else {
                            // User is logged but doesn't have the right permissions.
                            this.onForbidden().process(exchange);
                            return;
                        }
                    } else {
                        // User is not logged while required.
                        this.onUnauthorized().process(exchange);
                        return;
                    }
                }
            }
            context.execute();
        } else {
            final Result result = this.onNotFound();
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
     * @return the result
     */
    public Result onNotFound() {
        return this.notFound("Not Found");
    }

    /**
     * On unauthorized result.
     *
     * @return the result
     */
    public Result onUnauthorized() {
        return this.unauthorized("Unauthorized");
    }

    /**
     * On forbidden result.
     *
     * @return the result
     */
    public Result onForbidden() {
        return this.forbidden("Forbidden");
    }

    /**
     * On exception result.
     *
     * @param exception the exception
     * @return the result
     */
    public Result onException(final Throwable exception) {
        return this.internalServerError("Internal Server Error");
    }
}
