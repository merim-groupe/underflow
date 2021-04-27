package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.FlowHandler;
import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.routing.Name;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import io.undertow.server.HttpServerExchange;

import java.util.UUID;

/**
 * MyHandler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class MySubHandler extends FlowHandler {

    @GET
    @Path("/(?<uid>[0-9a-f-]+)/(?<name>[a-zA-Z0-9]+)")
    public void simplyGetMyPage(final HttpServerExchange exchange,
                                @Name("name") final String name,
                                @Name("uid") final UUID uid) {
        exchange.getResponseSender().send("<b>GNARF !</b> " + name + " : " + uid.toString());
    }
}
