package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.FlowHandler;
import com.merimdigitalmedia.underflow.annotation.method.*;
import com.merimdigitalmedia.underflow.annotation.routing.*;
import com.merimdigitalmedia.underflow.forms.WebForm;
import com.merimdigitalmedia.underflow.tests.entities.TestForm;
import io.undertow.server.HttpServerExchange;

import java.util.List;

/**
 * The Test handler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class TestHandler extends FlowHandler implements WebForm {

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
    @POST
    @Path("/webform")
    public void webForm(final HttpServerExchange exchange) throws Exception {
        this.dispatchAndBlock(exchange, () -> {
            this.getForm(exchange, TestForm.class, form -> {
                this.logger.error("GOT : {}", form.getName());
            }, exception -> {
                this.logger.error("OH NO ! ... Anyway ...", exception);
            });
        });
    }

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @GET
    @Path("/enum")
    public void webForm(final HttpServerExchange exchange,
                        @Query(value = "state", required = true) final StateEnum state) throws Exception {
        this.dispatchAndBlock(exchange, () -> {
            this.ok(exchange, sender -> {
                sender.send("State is : " + state.name());
            });
        });
    }

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @GET
    @Path("/enums")
    public void enums(final HttpServerExchange exchange,
                      @Query(value = "state",
                              listProperty = @QueryListProperty(backedType = StateEnum.class)) final List<StateEnum> states) throws Exception {
        this.dispatchAndBlock(exchange, () -> {
            this.ok(exchange, sender -> {
                final StringBuilder s = new StringBuilder("State are :");
                for (final StateEnum state : states) {
                    s.append("\n").append(state.name());
                }
                sender.send(s.toString());
            });
        });
    }

    /**
     * Simple GET fallback example.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @GET
    @Path("/enums-default")
    public void enumsDefault(final HttpServerExchange exchange,
                             @Query(value = "state",
                                     listProperty = @QueryListProperty(backedType = StateEnum.class),
                                     defaultValue = @DefaultValue({"DONE", "PENDING"})
                             ) final List<StateEnum> states) throws Exception {
        this.enums(exchange, states);
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
            this.ok(exchange, sender -> sender.send("Fallback"));
        });
    }
}
