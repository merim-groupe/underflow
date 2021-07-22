package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.FlowHandler;
import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.routing.Name;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import com.merimdigitalmedia.underflow.annotation.routing.Query;
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
     */
    @GET
    @Path("/(?<uuid>[0-9a-f]{8}-(?>[0-9a-f]{4}-){3}[0-9a-f]{12})")
    public void path(final HttpServerExchange exchange,
                     @Name("uuid") final UUID uuid,
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
                              @Query(value = "bar", required = true) final String bar) {
        exchange.getResponseSender().send("You called " + exchange.getRequestPath() + " with query parameter: " + bar);
    }
}
