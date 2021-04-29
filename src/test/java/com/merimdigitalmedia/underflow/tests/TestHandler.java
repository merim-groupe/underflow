package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.FlowHandler;
import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import io.undertow.server.HttpServerExchange;

/**
 * The Test handler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class TestHandler extends FlowHandler {

    /**
     * Simple GET example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @GET
    @Path("/foo")
    public void foo(final HttpServerExchange exchange) throws Exception {
        new SubTestHandler().handleRequest(exchange);
    }
}
