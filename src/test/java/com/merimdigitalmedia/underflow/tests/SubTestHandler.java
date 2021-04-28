package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.FlowHandler;
import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.routing.Name;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import com.merimdigitalmedia.underflow.annotation.routing.Query;
import io.undertow.server.HttpServerExchange;

import java.util.UUID;

/**
 * MyHandler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class SubTestHandler extends FlowHandler {

    @GET
    @Path("/(?<uid>[0-9a-f-]+)")
    @Query(parameters = "name")
    public void simplyGetMyPage(final HttpServerExchange exchange,
                                @Name("name") final String name,
                                @Name("uid") final UUID uid) {
        System.out.printf("Call came in %s", this.getClass().getName());
        exchange.getResponseSender().send("You called uid:" + uid.toString() + " and name: " + name);
    }
}
