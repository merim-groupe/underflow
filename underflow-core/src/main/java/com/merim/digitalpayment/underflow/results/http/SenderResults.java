package com.merim.digitalpayment.underflow.results.http;

import io.undertow.io.Sender;
import io.undertow.util.StatusCodes;

import java.util.function.Consumer;

/**
 * FlowAnswer.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public interface SenderResults {

    /**
     * End the request with a status 200 OK.
     *
     * @param exchangeData the exchange data
     * @return the result
     */
    default HttpResult ok(final Consumer<Sender> exchangeData) {
        return this.result(StatusCodes.OK, exchangeData);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchangeData the exchange data
     * @return the result
     */
    default HttpResult created(final Consumer<Sender> exchangeData) {
        return this.result(StatusCodes.CREATED, exchangeData);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchangeData the exchange data
     * @return the result
     */
    default HttpResult badRequest(final Consumer<Sender> exchangeData) {
        return this.result(StatusCodes.BAD_REQUEST, exchangeData);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param exchangeData the exchange data
     * @return the result
     */
    default HttpResult unauthorized(final Consumer<Sender> exchangeData) {
        return this.result(StatusCodes.UNAUTHORIZED, exchangeData);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchangeData the exchange data
     * @return the result
     */
    default HttpResult forbidden(final Consumer<Sender> exchangeData) {
        return this.result(StatusCodes.FORBIDDEN, exchangeData);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchangeData the exchange data
     * @return the result
     */
    default HttpResult notFound(final Consumer<Sender> exchangeData) {
        return this.result(StatusCodes.NOT_FOUND, exchangeData);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param exchangeData the exchange data
     * @return the result
     */
    default HttpResult internalServerError(final Consumer<Sender> exchangeData) {
        return this.result(StatusCodes.INTERNAL_SERVER_ERROR, exchangeData);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param exchangeData the exchange data
     * @return the result
     */
    default HttpResult serviceUnavailable(final Consumer<Sender> exchangeData) {
        return this.result(StatusCodes.SERVICE_UNAVAILABLE, exchangeData);
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
