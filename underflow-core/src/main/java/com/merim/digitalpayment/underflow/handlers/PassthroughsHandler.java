package com.merim.digitalpayment.underflow.handlers;

import com.merim.digitalpayment.underflow.mdc.MDCContext;
import com.merim.digitalpayment.underflow.mdc.MDCInterceptor;
import com.merim.digitalpayment.underflow.mdc.MDCServerContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * PassthroughHandler.
 *
 * @author Pierre Adam
 * @since 21.07.22
 */
public abstract class PassthroughsHandler implements HttpHandler, MDCContext {

    /**
     * The Underlying.
     */
    protected final HttpHandler underlying;

    /**
     * Instantiates a new Passthrough handler.
     *
     * @param underlying the underlying
     */
    public PassthroughsHandler(final HttpHandler underlying) {
        this.underlying = underlying;
    }

    /**
     * Intercept request.
     *
     * @param exchange the exchange
     */
    protected abstract void interceptRequest(final HttpServerExchange exchange);

    /**
     * Call underlying.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    protected void callUnderlying(final HttpServerExchange exchange) throws Exception {
        this.underlying.handleRequest(exchange);
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        try (final MDCServerContext ignored = MDCInterceptor.getInstance().withMDCServerContext(exchange)) {
            this.interceptRequest(exchange);
            this.callUnderlying(exchange);
        }
    }

    /**
     * Gets backed handler.
     *
     * @return the backed handler
     */
    protected HttpHandler getFinalBackedHandler() {
        if (this.underlying instanceof PassthroughsHandler) {
            return ((PassthroughsHandler) this.underlying).getFinalBackedHandler();
        }
        return this.underlying;
    }
}
