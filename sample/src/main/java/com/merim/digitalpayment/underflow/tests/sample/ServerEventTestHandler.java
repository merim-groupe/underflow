package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.annotation.method.GET;
import com.merim.digitalpayment.underflow.annotation.routing.Path;
import com.merim.digitalpayment.underflow.annotation.routing.Query;
import com.merim.digitalpayment.underflow.handlers.flows.FlowApiHandler;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.results.ServerEventResult;
import io.undertow.server.handlers.sse.ServerSentEventHandler;

import java.io.IOException;

/**
 * ServerEventTestHandler.
 *
 * @author Pierre Adam
 * @since 21.11.22
 */
public class ServerEventTestHandler extends FlowApiHandler {

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
     * @return the result
     */
    @GET
    @Path("/connect")
    public Result connect() {
        return new ServerEventResult(this.sseh);
    }

    /**
     * Broadcast.
     *
     * @param message the message
     * @return the result
     */
    @GET
    @Path("/broadcast")
    public Result broadcast(@Query(value = "message", required = true) final String message) {
        this.sseh.getConnections().forEach(connection -> {
            connection.send(message);
        });
        return this.ok("Message sent !");
    }

    /**
     * Disconnect.
     *
     * @return the result
     */
    @GET
    @Path("/disconnect")
    public Result disconnect() {
        this.sseh.getConnections().forEach(connection -> {
            try {
                connection.close();
            } catch (final IOException e) {
                this.logger.error("Error while closing the connection", e);
            }
        });
        return this.ok("Clients disconnected !");
    }
}
