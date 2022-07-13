package com.merimdigitalmedia.underflow.handlers.flows.answers;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * FlowAnswer.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public interface FlowStandardAnswer {

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void ok(final HttpServerExchange exchange, final String data) {
        this.result(exchange, 200, data);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void ok(final HttpServerExchange exchange, final String data, final IoCallback ioCallback) {
        this.result(exchange, 200, data, ioCallback);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void ok(final HttpServerExchange exchange, final ByteBuffer data) {
        this.result(exchange, 200, data);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void ok(final HttpServerExchange exchange, final ByteBuffer data, final IoCallback ioCallback) {
        this.result(exchange, 200, data, ioCallback);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void ok(final HttpServerExchange exchange, final ByteBuffer[] data) {
        this.result(exchange, 200, data);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void ok(final HttpServerExchange exchange, final ByteBuffer[] data, final IoCallback ioCallback) {
        this.result(exchange, 200, data, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void created(final HttpServerExchange exchange, final String data) {
        this.result(exchange, 201, data);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void created(final HttpServerExchange exchange, final String data, final IoCallback ioCallback) {
        this.result(exchange, 201, data, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void created(final HttpServerExchange exchange, final ByteBuffer data) {
        this.result(exchange, 201, data);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void created(final HttpServerExchange exchange, final ByteBuffer data, final IoCallback ioCallback) {
        this.result(exchange, 201, data, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void created(final HttpServerExchange exchange, final ByteBuffer[] data) {
        this.result(exchange, 201, data);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void created(final HttpServerExchange exchange, final ByteBuffer data[], final IoCallback ioCallback) {
        this.result(exchange, 201, data, ioCallback);
    }

    /**
     * End the request with a status 204 No Content.
     *
     * @param exchange the exchange
     */
    default void noContent(final HttpServerExchange exchange) {
        this.result(exchange, 204, sender -> {
        });
    }

    /**
     * End the request with a status 204 No Content.
     *
     * @param exchange   the exchange
     * @param ioCallback the io callback
     */
    default void noContent(final HttpServerExchange exchange, final IoCallback ioCallback) {
        this.result(exchange, 204, sender -> {
            sender.send("", ioCallback);
        });
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void badRequest(final HttpServerExchange exchange, final String data) {
        this.result(exchange, 400, data);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void badRequest(final HttpServerExchange exchange, final String data, final IoCallback ioCallback) {
        this.result(exchange, 400, data, ioCallback);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void badRequest(final HttpServerExchange exchange, final ByteBuffer data) {
        this.result(exchange, 400, data);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void badRequest(final HttpServerExchange exchange, final ByteBuffer data, final IoCallback ioCallback) {
        this.result(exchange, 400, data, ioCallback);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void badRequest(final HttpServerExchange exchange, final ByteBuffer[] data) {
        this.result(exchange, 400, data);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void badRequest(final HttpServerExchange exchange, final ByteBuffer[] data, final IoCallback ioCallback) {
        this.result(exchange, 400, data, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void forbidden(final HttpServerExchange exchange, final String data) {
        this.result(exchange, 403, data);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void forbidden(final HttpServerExchange exchange, final String data, final IoCallback ioCallback) {
        this.result(exchange, 403, data, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void forbidden(final HttpServerExchange exchange, final ByteBuffer data) {
        this.result(exchange, 403, data);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void forbidden(final HttpServerExchange exchange, final ByteBuffer data, final IoCallback ioCallback) {
        this.result(exchange, 403, data, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void forbidden(final HttpServerExchange exchange, final ByteBuffer[] data) {
        this.result(exchange, 403, data);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void forbidden(final HttpServerExchange exchange, final ByteBuffer[] data, final IoCallback ioCallback) {
        this.result(exchange, 403, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void notFound(final HttpServerExchange exchange, final String data) {
        this.result(exchange, 404, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void notFound(final HttpServerExchange exchange, final String data, final IoCallback ioCallback) {
        this.result(exchange, 404, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void notFound(final HttpServerExchange exchange, final ByteBuffer data) {
        this.result(exchange, 404, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void notFound(final HttpServerExchange exchange, final ByteBuffer data, final IoCallback ioCallback) {
        this.result(exchange, 404, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void notFound(final HttpServerExchange exchange, final ByteBuffer[] data) {
        this.result(exchange, 404, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void notFound(final HttpServerExchange exchange, final ByteBuffer[] data, final IoCallback ioCallback) {
        this.result(exchange, 404, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void internalServerError(final HttpServerExchange exchange, final String data) {
        this.result(exchange, 500, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void internalServerError(final HttpServerExchange exchange, final String data, final IoCallback ioCallback) {
        this.result(exchange, 500, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void internalServerError(final HttpServerExchange exchange, final ByteBuffer data) {
        this.result(exchange, 500, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void internalServerError(final HttpServerExchange exchange, final ByteBuffer data, final IoCallback ioCallback) {
        this.result(exchange, 500, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void internalServerError(final HttpServerExchange exchange, final ByteBuffer[] data) {
        this.result(exchange, 500, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void internalServerError(final HttpServerExchange exchange, final ByteBuffer[] data, final IoCallback ioCallback) {
        this.result(exchange, 500, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final String data) {
        this.result(exchange, 503, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final String data, final IoCallback ioCallback) {
        this.result(exchange, 503, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final ByteBuffer data) {
        this.result(exchange, 503, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final ByteBuffer data, final IoCallback ioCallback) {
        this.result(exchange, 503, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange the exchange
     * @param data     the data
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final ByteBuffer[] data) {
        this.result(exchange, 503, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param data       the data
     * @param ioCallback the io callback
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final ByteBuffer[] data, final IoCallback ioCallback) {
        this.result(exchange, 503, data, ioCallback);
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange the exchange
     * @param code     the code
     * @param data     the exchange data
     */
    default void result(final HttpServerExchange exchange, final int code, final String data) {
        this.result(exchange, code, sender -> sender.send(data));
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange   the exchange
     * @param code       the code
     * @param data       the exchange data
     * @param ioCallback the io callback
     */
    default void result(final HttpServerExchange exchange, final int code, final String data, final IoCallback ioCallback) {
        this.result(exchange, code, sender -> sender.send(data, ioCallback));
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange the exchange
     * @param code     the code
     * @param data     the exchange data
     */
    default void result(final HttpServerExchange exchange, final int code, final ByteBuffer data) {
        this.result(exchange, code, sender -> sender.send(data));
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange   the exchange
     * @param code       the code
     * @param data       the exchange data
     * @param ioCallback the io callback
     */
    default void result(final HttpServerExchange exchange, final int code, final ByteBuffer data, final IoCallback ioCallback) {
        this.result(exchange, code, sender -> sender.send(data, ioCallback));
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange the exchange
     * @param code     the code
     * @param data     the exchange data
     */
    default void result(final HttpServerExchange exchange, final int code, final ByteBuffer[] data) {
        this.result(exchange, code, sender -> sender.send(data));
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange   the exchange
     * @param code       the code
     * @param data       the exchange data
     * @param ioCallback the io callback
     */
    default void result(final HttpServerExchange exchange, final int code, final ByteBuffer[] data, final IoCallback ioCallback) {
        this.result(exchange, code, sender -> sender.send(data, ioCallback));
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
