package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.handlers.context.ContextHandler;
import com.merim.digitalpayment.underflow.handlers.flows.exceptions.InvalidMethodException;
import com.merim.digitalpayment.underflow.mdc.MDCContext;
import com.merim.digitalpayment.underflow.mdc.MDCInterceptor;
import com.merim.digitalpayment.underflow.mdc.MDCServerContext;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.results.http.HttpResult;
import com.merim.digitalpayment.underflow.results.http.SenderHttpResult;
import com.merim.digitalpayment.underflow.results.http.SenderResults;
import com.merim.digitalpayment.underflow.results.http.StandardResults;
import com.merim.digitalpayment.underflow.security.FlowSecurity;
import com.merim.digitalpayment.underflow.security.annotations.Secured;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    protected final Logger logger;

    /**
     * The Flow security.
     */
    private final FlowSecurity<?, ?> flowSecurity;

    /**
     * The Handler info.
     */
    @Getter
    private final FlowHandlerInfo handlerInfo;

    /**
     * The Methods.
     */
    private final List<FlowMethodInfo> methodsInfo;

    /**
     * Instantiates a new Flow handler.
     */
    public FlowHandler() {
        this(null);
    }

    /**
     * Instantiates a new Flow handler.
     *
     * @param flowSecurity the flow security
     */
    public FlowHandler(final FlowSecurity<?, ?> flowSecurity) {
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.flowSecurity = flowSecurity;
        this.handlerInfo = FlowHandlerInfo.create(this.getClass(), this);
        this.methodsInfo = FlowHandler.initMethodsInfo(this.handlerInfo);
    }

    /**
     * Init methods info list.
     *
     * @param handlerInfo the handler info
     * @return the list
     */
    private static List<FlowMethodInfo> initMethodsInfo(final FlowHandlerInfo handlerInfo) {
        final List<FlowMethodInfo> results = new ArrayList<>();

        final Method[] methods = handlerInfo.getHandlerClass().getDeclaredMethods();
        for (final Method method : methods) {
            try {
                final FlowMethodInfo flowMethodInfo = new FlowMethodInfo(handlerInfo, method);
                results.add(flowMethodInfo);
            } catch (final InvalidMethodException ignore) {
            }
        }

        return results;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) {
        try (final MDCServerContext ignored = MDCInterceptor.getInstance().withMDCServerContext(exchange)) {
            final ContextHandler context = new ContextHandler(this, exchange);

            if (context.isValid()) {
                final Optional<Secured> secured = context.requireSecurity();
                context.addInjectable(this.flowSecurity);
                if (this.flowSecurity != null) {
                    final Optional<?> optionalUser = this.flowSecurity.isLogged(exchange);
                    context.addInjectableUnsafe(this.flowSecurity.userRepresentationClass(), optionalUser.orElse(null));

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
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets methods for.
     *
     * @return the methods for
     */
    public Collection<FlowMethodInfo> getMethodsInfo() {
        return this.methodsInfo;
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
