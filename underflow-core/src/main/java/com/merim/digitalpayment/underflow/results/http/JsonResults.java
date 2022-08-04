package com.merim.digitalpayment.underflow.results.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.merim.digitalpayment.underflow.utils.Application;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.util.StatusCodes;

import java.util.function.Consumer;

/**
 * JsonResults.
 *
 * @author Pierre Adam
 * @since 22.07.20
 */
public interface JsonResults {

    /**
     * End the request with a status 200 OK.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult ok(final JsonNode data) {
        return this.result(StatusCodes.OK, data);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult ok(final JsonNode data, final IoCallback ioCallback) {
        return this.result(StatusCodes.OK, data, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult created(final JsonNode data) {
        return this.result(StatusCodes.CREATED, data);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult created(final JsonNode data, final IoCallback ioCallback) {
        return this.result(StatusCodes.CREATED, data, ioCallback);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult badRequest(final JsonNode data) {
        return this.result(StatusCodes.BAD_REQUEST, data);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult badRequest(final JsonNode data, final IoCallback ioCallback) {
        return this.result(StatusCodes.BAD_REQUEST, data, ioCallback);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult unauthorized(final JsonNode data) {
        return this.result(StatusCodes.UNAUTHORIZED, data);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult unauthorized(final JsonNode data, final IoCallback ioCallback) {
        return this.result(StatusCodes.UNAUTHORIZED, data, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult forbidden(final JsonNode data) {
        return this.result(StatusCodes.FORBIDDEN, data);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult forbidden(final JsonNode data, final IoCallback ioCallback) {
        return this.result(StatusCodes.FORBIDDEN, data, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult notFound(final JsonNode data) {
        return this.result(StatusCodes.NOT_FOUND, data);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult notFound(final JsonNode data, final IoCallback ioCallback) {
        return this.result(StatusCodes.NOT_FOUND, data, ioCallback);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult internalServerError(final JsonNode data) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, data);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult internalServerError(final JsonNode data, final IoCallback ioCallback) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, data, ioCallback);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param data the data
     * @return the result
     */
    default HttpResult serviceUnavailable(final JsonNode data) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, data);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param data       the data
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult serviceUnavailable(final JsonNode data, final IoCallback ioCallback) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, data, ioCallback);
    }

    /**
     * Ends the request with the given status.
     *
     * @param code        the code
     * @param jsonContent the json content
     * @return the result
     */
    default HttpResult result(final int code, final JsonNode jsonContent) {
        return this.result(code, jsonContent, IoCallback.END_EXCHANGE);
    }

    /**
     * Ends the request with the given status.
     *
     * @param code        the code
     * @param jsonContent the json content
     * @param ioCallback  the io callback
     * @return the result
     */
    default HttpResult result(final int code, final JsonNode jsonContent, final IoCallback ioCallback) {
        try {
            final String data = Application.getMapper().writeValueAsString(jsonContent);
            return this.result(code, sender -> sender.send(data, ioCallback))
                    .withContentType("application/json");
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Transform object to a JsonNode.
     *
     * @param object the object
     * @return the t
     */
    default JsonNode toJsonNode(final Object object) {
        return Application.getMapper().valueToTree(object);
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
