package com.merimdigitalmedia.underflow.handlers;

import com.merimdigitalmedia.underflow.mdc.MDCContext;
import com.merimdigitalmedia.underflow.mdc.MDCInterceptor;
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
        MDCInterceptor.getInstance().accept(exchange);
        this.interceptRequest(exchange);
        this.callUnderlying(exchange);
    }
}
