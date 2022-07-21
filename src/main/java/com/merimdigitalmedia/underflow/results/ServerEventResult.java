package com.merimdigitalmedia.underflow.results;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.sse.ServerSentEventHandler;

/**
 * ServerEventResult.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
public class ServerEventResult implements Result {

    /**
     * The Server sent event handler.
     */
    private final ServerSentEventHandler serverSentEventHandler;

    /**
     * Instantiates a new Server event result.
     *
     * @param serverSentEventHandler the server sent event handler
     */
    public ServerEventResult(final ServerSentEventHandler serverSentEventHandler) {
        this.serverSentEventHandler = serverSentEventHandler;
    }

    @Override
    public void process(final HttpServerExchange exchange) {
        try {
            this.serverSentEventHandler.handleRequest(exchange);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
