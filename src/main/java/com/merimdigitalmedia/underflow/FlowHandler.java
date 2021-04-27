package com.merimdigitalmedia.underflow;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

/**
 * V2.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class FlowHandler implements HttpHandler {

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        final ContextHandler context = new ContextHandler(this, exchange);

        if (context.isValid()) {
            context.execute();
        } else {
            exchange.setStatusCode(404)
                    .endExchange();
        }
    }
}
