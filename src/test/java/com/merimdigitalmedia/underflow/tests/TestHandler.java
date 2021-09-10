package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.FlowHandler;
import com.merimdigitalmedia.underflow.annotation.method.*;
import com.merimdigitalmedia.underflow.annotation.routing.Fallback;
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
     */
    @GET
    @Path("")
    public void home(final HttpServerExchange exchange) {
        this.dispatchAndBlock(exchange, () -> this.ok(exchange, sender -> sender.send("Hello Underflow !")));
    }

    /**
     * Post home.
     *
     * @param exchange the exchange
     */
    @POST
    @Path("")
    public void postHome(final HttpServerExchange exchange) {
        this.dispatchAndBlock(exchange, () -> this.ok(exchange, sender -> sender.send("POST from Underflow")));
    }

    /**
     * Put home.
     *
     * @param exchange the exchange
     */
    @PUT
    @Path("")
    public void putHome(final HttpServerExchange exchange) {
        this.dispatchAndBlock(exchange, () -> this.ok(exchange, sender -> sender.send("PUT from Underflow")));
    }

    /**
     * Patch home.
     *
     * @param exchange the exchange
     */
    @PATCH
    @Path("")
    public void patchHome(final HttpServerExchange exchange) {
        this.dispatchAndBlock(exchange, () -> this.ok(exchange, sender -> sender.send("PATCH from Underflow")));
    }

    /**
     * Option home.
     *
     * @param exchange the exchange
     */
    @OPTIONS
    @Path("")
    public void optionHome(final HttpServerExchange exchange) {
        this.dispatchAndBlock(exchange, () -> this.ok(exchange, sender -> sender.send("OPTION from Underflow")));
    }

    /**
     * Delete home.
     *
     * @param exchange the exchange
     */
    @DELETE
    @Path("")
    public void deleteHome(final HttpServerExchange exchange) {
        this.dispatchAndBlock(exchange, () -> this.ok(exchange, sender -> sender.send("DELETE from Underflow")));
    }

    /**
     * Delete home.
     *
     * @param exchange the exchange
     */
    @HEAD
    @Path("")
    public void headHome(final HttpServerExchange exchange) {
        this.dispatchAndBlock(exchange, () -> this.ok(exchange, sender -> sender.send("HEAD from Underflow")));
    }

    /**
     * Simple GET example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @ALL
    @Path("/foo")
    public void foo(final HttpServerExchange exchange) throws Exception {
        new SubTestHandler().handleRequest(exchange);
    }

    /**
     * Simple GET example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @GET
    @Path("/bar1")
    @Path("/bar2")
    public void bar(final HttpServerExchange exchange) throws Exception {
        new SubTestHandler().handleRequest(exchange);
    }

    /**
     * Simple GET example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @GET
    @Path("/status")
    @Path("/statusBis")
    public void status(final HttpServerExchange exchange) throws Exception {
        exchange.getResponseSender().send("OK !");
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
        this.dispatchAndBlock(exchange, () -> {
        });
//        this.ok(exchange, sender -> sender.send("Fallback"));
    }
}
