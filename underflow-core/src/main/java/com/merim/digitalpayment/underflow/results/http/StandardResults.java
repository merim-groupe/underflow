package com.merim.digitalpayment.underflow.results.http;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * FlowAnswer.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public interface StandardResults {

    /**
     * End the request with a status 200 OK.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult ok(final String data) {
        return this.result(StatusCodes.OK, data);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult ok(final String data, final IoCallback ioCallback) {
        return this.result(StatusCodes.OK, data, ioCallback);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult ok(final ByteBuffer data) {
        return this.result(StatusCodes.OK, data);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult ok(final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(StatusCodes.OK, data, ioCallback);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult ok(final ByteBuffer[] data) {
        return this.result(StatusCodes.OK, data);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult ok(final ByteBuffer[] data, final IoCallback ioCallback) {
        return this.result(StatusCodes.OK, data, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult created(final String data) {
        return this.result(StatusCodes.CREATED, data);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult created(final String data, final IoCallback ioCallback) {
        return this.result(StatusCodes.CREATED, data, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult created(final ByteBuffer data) {
        return this.result(StatusCodes.CREATED, data);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult created(final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(StatusCodes.CREATED, data, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult created(final ByteBuffer[] data) {
        return this.result(StatusCodes.CREATED, data);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult created(final ByteBuffer data[], final IoCallback ioCallback) {
        return this.result(StatusCodes.CREATED, data, ioCallback);
    }

    /**
     * End the request with a status 204 No Content.
     *
     * @return the result
     */
    default HttpResult noContent() {
        return this.result(StatusCodes.NO_CONTENT, sender -> {
        });
    }

    /**
     * End the request with a status 204 No Content.
     *
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult noContent(final IoCallback ioCallback) {
        return this.result(StatusCodes.NO_CONTENT, sender -> {
            sender.send("", ioCallback);
        });
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult badRequest(final String data) {
        return this.result(StatusCodes.BAD_REQUEST, data);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult badRequest(final String data, final IoCallback ioCallback) {
        return this.result(StatusCodes.BAD_REQUEST, data, ioCallback);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult badRequest(final ByteBuffer data) {
        return this.result(StatusCodes.BAD_REQUEST, data);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult badRequest(final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(StatusCodes.BAD_REQUEST, data, ioCallback);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult badRequest(final ByteBuffer[] data) {
        return this.result(StatusCodes.BAD_REQUEST, data);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult badRequest(final ByteBuffer[] data, final IoCallback ioCallback) {
        return this.result(StatusCodes.BAD_REQUEST, data, ioCallback);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult unauthorized(final String data) {
        return this.result(StatusCodes.UNAUTHORIZED, data);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult unauthorized(final String data, final IoCallback ioCallback) {
        return this.result(StatusCodes.UNAUTHORIZED, data, ioCallback);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult unauthorized(final ByteBuffer data) {
        return this.result(StatusCodes.UNAUTHORIZED, data);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult unauthorized(final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(StatusCodes.UNAUTHORIZED, data, ioCallback);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult unauthorized(final ByteBuffer[] data) {
        return this.result(StatusCodes.UNAUTHORIZED, data);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult unauthorized(final ByteBuffer[] data, final IoCallback ioCallback) {
        return this.result(StatusCodes.UNAUTHORIZED, data, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult forbidden(final String data) {
        return this.result(StatusCodes.FORBIDDEN, data);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult forbidden(final String data, final IoCallback ioCallback) {
        return this.result(StatusCodes.FORBIDDEN, data, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult forbidden(final ByteBuffer data) {
        return this.result(StatusCodes.FORBIDDEN, data);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult forbidden(final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(StatusCodes.FORBIDDEN, data, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult forbidden(final ByteBuffer[] data) {
        return this.result(StatusCodes.FORBIDDEN, data);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult forbidden(final ByteBuffer[] data, final IoCallback ioCallback) {
        return this.result(StatusCodes.FORBIDDEN, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult notFound(final String data) {
        return this.result(StatusCodes.NOT_FOUND, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult notFound(final String data, final IoCallback ioCallback) {
        return this.result(StatusCodes.NOT_FOUND, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult notFound(final ByteBuffer data) {
        return this.result(StatusCodes.NOT_FOUND, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult notFound(final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(StatusCodes.NOT_FOUND, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult notFound(final ByteBuffer[] data) {
        return this.result(StatusCodes.NOT_FOUND, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult notFound(final ByteBuffer[] data, final IoCallback ioCallback) {
        return this.result(StatusCodes.NOT_FOUND, data, ioCallback);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult internalServerError(final String data) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, data);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult internalServerError(final String data, final IoCallback ioCallback) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, data, ioCallback);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult internalServerError(final ByteBuffer data) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, data);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult internalServerError(final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, data, ioCallback);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult internalServerError(final ByteBuffer[] data) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, data);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult internalServerError(final ByteBuffer[] data, final IoCallback ioCallback) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, data, ioCallback);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult serviceUnavailable(final String data) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, data);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult serviceUnavailable(final String data, final IoCallback ioCallback) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, data, ioCallback);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult serviceUnavailable(final ByteBuffer data) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, data);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult serviceUnavailable(final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, data, ioCallback);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult serviceUnavailable(final ByteBuffer[] data) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, data);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult serviceUnavailable(final ByteBuffer[] data, final IoCallback ioCallback) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, data, ioCallback);
    }

    /**
     * End the request with a status 302 Temporary Redirect.
     *
     * @param location the location
     * @return the http result
     */
    default HttpResult redirect(final String location) {
        return this.result(StatusCodes.FOUND, "")
                .withHeader(Headers.LOCATION, location);
    }

    /**
     * End the request with a status 302 Temporary Redirect.
     *
     * @param location   the location
     * @param ioCallback the io callback
     * @return the http result
     */
    default HttpResult redirect(final String location, final IoCallback ioCallback) {
        return this.result(StatusCodes.FOUND, "", ioCallback)
                .withHeader(Headers.LOCATION, location);
    }

    /**
     * End the request with a status 301 Moved Permanently.
     *
     * @param location the location
     * @return the http result
     */
    default HttpResult redirectPermanently(final String location) {
        return this.result(StatusCodes.MOVED_PERMANENTLY, "")
                .withHeader(Headers.LOCATION, location);
    }

    /**
     * End the request with a status 301 Moved Permanently.
     *
     * @param location   the location
     * @param ioCallback the io callback
     * @return the http result
     */
    default HttpResult redirectPermanently(final String location, final IoCallback ioCallback) {
        return this.result(StatusCodes.MOVED_PERMANENTLY, "", ioCallback)
                .withHeader(Headers.LOCATION, location);
    }

    /**
     * Ends the request with the given status.
     *
     * @param code the code
     * @param data the exchange data
     * @return the result
     */
    default HttpResult result(final int code, final String data) {
        return this.result(code, sender -> sender.send(data));
    }

    /**
     * Ends the request with the given status.
     *
     * @param code       the code
     * @param data       the exchange data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult result(final int code, final String data, final IoCallback ioCallback) {
        return this.result(code, sender -> sender.send(data, ioCallback));
    }

    /**
     * Ends the request with the given status.
     *
     * @param code the code
     * @param data the exchange data
     * @return the result
     */
    default HttpResult result(final int code, final ByteBuffer data) {
        return this.result(code, sender -> sender.send(data));
    }

    /**
     * Ends the request with the given status.
     *
     * @param code       the code
     * @param data       the exchange data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult result(final int code, final ByteBuffer data, final IoCallback ioCallback) {
        return this.result(code, sender -> sender.send(data, ioCallback));
    }

    /**
     * Ends the request with the given status.
     *
     * @param code the code
     * @param data the exchange data
     * @return the result
     */
    default HttpResult result(final int code, final ByteBuffer[] data) {
        return this.result(code, sender -> sender.send(data));
    }

    /**
     * Ends the request with the given status.
     *
     * @param code       the code
     * @param data       the exchange data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult result(final int code, final ByteBuffer[] data, final IoCallback ioCallback) {
        return this.result(code, sender -> sender.send(data, ioCallback));
    }

    /**
     * Ends the request with the given status.
     *
     * @param code         the code
     * @param exchangeData the exchange data
     * @return the result
     */
    HttpResult result(final int code, final Consumer<Sender> exchangeData);
}
