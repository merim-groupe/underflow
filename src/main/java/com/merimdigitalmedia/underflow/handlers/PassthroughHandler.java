package com.merimdigitalmedia.underflow.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * PassthroughHandler.
 *
 * @author Pierre Adam
 * @since 21.07.22
 */
public abstract class PassthroughHandler implements HttpHandler {

    /**
     * The Underlying.
     */
    protected final HttpHandler underlying;

    /**
     * Instantiates a new Passthrough handler.
     *
     * @param underlying the underlying
     */
    public PassthroughHandler(final HttpHandler underlying) {
        this.underlying = underlying;
    }

    /**
     * Intercept request.
     *
     * @param exchange the exchange
     */
    protected abstract void interceptRequest(final HttpServerExchange exchange);

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        this.interceptRequest(exchange);
        this.underlying.handleRequest(exchange);
    }
}
