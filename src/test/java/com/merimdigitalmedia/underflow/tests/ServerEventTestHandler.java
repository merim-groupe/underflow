package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import com.merimdigitalmedia.underflow.annotation.routing.Query;
import com.merimdigitalmedia.underflow.api.ApiHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.sse.ServerSentEventHandler;

import java.io.IOException;

/**
 * ServerEventTestHandler.
 *
 * @author Pierre Adam
 * @since 21.11.22
 */
public class ServerEventTestHandler extends ApiHandler {

    /**
     * The Sseh.
     */
    private final ServerSentEventHandler sseh;

    /**
     * Instantiates a new Server event handler.
     */
    public ServerEventTestHandler() {
        this.sseh = new ServerSentEventHandler();
    }

    /**
     * Connect.
     *
     * @param exchange the exchange
     */
    @GET
    @Path("/connect")
    public void connect(final HttpServerExchange exchange) {
        this.dispatchUnsafeAndBlock(exchange, () -> {
            try {
                this.sseh.handleRequest(exchange);
            } catch (final Exception e) {
                this.logger.error("Unable to handler request !", e);
            }
        });
    }

    /**
     * Broadcast.
     *
     * @param exchange the exchange
     * @param message  the message
     */
    @GET
    @Path("/broadcast")
    public void broadcast(final HttpServerExchange exchange, @Query(value = "message", required = true) final String message) {
        this.dispatchAndBlock(exchange, () ->
                this.sseh.getConnections().forEach(connection -> {
                    connection.send(message);
                })
        );
    }

    /**
     * Disconnect.
     *
     * @param exchange the exchange
     */
    @GET
    @Path("/disconnect")
    public void disconnect(final HttpServerExchange exchange) {
        this.dispatchAndBlock(exchange, () ->
                this.sseh.getConnections().forEach(connection -> {
                    try {
                        connection.close();
                    } catch (final IOException e) {
                        this.logger.error("Error while closing the connection", e);
                    }
                })
        );
    }
}
