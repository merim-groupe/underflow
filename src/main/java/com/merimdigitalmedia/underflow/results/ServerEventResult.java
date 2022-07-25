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
     * The And then.
     */
    private final Runnable andThen;

    /**
     * Instantiates a new Server event result.
     *
     * @param serverSentEventHandler the server sent event handler
     * @param andThen                the and then
     */
    public ServerEventResult(final ServerSentEventHandler serverSentEventHandler, final Runnable andThen) {
        this.serverSentEventHandler = serverSentEventHandler;
        this.andThen = andThen;
    }

    /**
     * Instantiates a new Server event result.
     *
     * @param serverSentEventHandler the server sent event handler
     */
    public ServerEventResult(final ServerSentEventHandler serverSentEventHandler) {
        this(serverSentEventHandler, null);
    }

    @Override
    public void process(final HttpServerExchange exchange) {
        try {
            this.serverSentEventHandler.handleRequest(exchange);
            this.andThen.run();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
