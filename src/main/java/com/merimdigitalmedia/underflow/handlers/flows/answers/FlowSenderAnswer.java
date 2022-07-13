package com.merimdigitalmedia.underflow.handlers.flows.answers;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

import java.util.function.Consumer;

/**
 * FlowAnswer.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public interface FlowSenderAnswer {

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    default void ok(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 200, exchangeData);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    default void created(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 201, exchangeData);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    default void badRequest(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 400, exchangeData);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    default void forbidden(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 403, exchangeData);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    default void notFound(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 404, exchangeData);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    default void internalServerError(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 500, exchangeData);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 503, exchangeData);
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange     the exchange
     * @param code         the code
     * @param exchangeData the exchange data
     */
    void result(final HttpServerExchange exchange, final int code, final Consumer<Sender> exchangeData);
}
