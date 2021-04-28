package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.FlowHandler;
import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import io.undertow.server.HttpServerExchange;

/**
 * MyHandler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class TestHandler extends FlowHandler {

    /**
     * Simply get my page.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @GET
    @Path("/root")
    public void simplyGetMyPage(final HttpServerExchange exchange) throws Exception {
        System.out.printf("Call came in %s", this.getClass().getName());
        new SubTestHandler().handleRequest(exchange);
    }
}
