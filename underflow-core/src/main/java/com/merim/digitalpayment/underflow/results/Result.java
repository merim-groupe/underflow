package com.merim.digitalpayment.underflow.results;

import io.undertow.server.HttpServerExchange;

/**
 * Result.
 *
 * @author Pierre Adam
 * @since 22.07.18
 */
public interface Result {

    /**
     * Process the result.
     *
     * @param exchange the exchange
     */
    void process(final HttpServerExchange exchange);
}
