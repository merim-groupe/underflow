package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.method.POST;
import com.merimdigitalmedia.underflow.annotation.routing.*;
import com.merimdigitalmedia.underflow.handlers.flows.FlowHandler;
import io.undertow.server.HttpServerExchange;

import java.util.UUID;

/**
 * The Sub test handler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class SubTestHandler extends FlowHandler {

    /**
     * GET example with parameters in the path.
     *
     * @param exchange the exchange
     * @param uuid     the uuid parameter from path
     * @param bar      the bar
     */
    @GET
    @Path("/(?<uuid>[0-9a-f]{8}-(?>[0-9a-f]{4}-){3}[0-9a-f]{12})")
    public void path(final HttpServerExchange exchange,
                     @Named("uuid") final UUID uuid,
                     @Query(value = "bar", required = true) final String bar) {
        exchange.getResponseSender().send("You called " + exchange.getRequestPath() + " with path parameter: " + uuid.toString() + " and query parameter: " + bar);
    }

    /**
     * GET example with parameters in the query string.
     *
     * @param exchange the exchange
     * @param bar      the bar parameter from query string
     */
    @GET
    @Path("/")
    public void pathWithQuery(final HttpServerExchange exchange,
                              @Query(value = "bar", defaultValue = @DefaultValue(value = "default value")) final String bar) {
        exchange.getResponseSender().send("You called " + exchange.getRequestPath() + " with query parameter: " + bar);
    }

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @GET
    @Fallback
    public void fallback(final HttpServerExchange exchange) throws Exception {
        this.ok(exchange, sender -> sender.send("Fallback"));
    }

    /**
     * GET example with parameters in the query string.
     *
     * @param exchange the exchange
     */
    @GET
    @Path("/ping")
    public void getPing(final HttpServerExchange exchange) {
        this.dispatch(exchange, () -> this.ok(exchange, sender -> sender.send("OK GET !")));
    }

    /**
     * GET example with parameters in the query string.
     *
     * @param exchange the exchange
     */
    @POST
    @Path("/ping")
    public void postPing(final HttpServerExchange exchange) {
        this.dispatch(exchange, () -> this.ok(exchange, sender -> sender.send("OK POST !")));
    }
}
