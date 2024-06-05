package com.merim.digitalpayment.underflow.results;

import io.undertow.server.HttpServerExchange;

import java.lang.reflect.Method;

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
     * @param method   the method
     */
    void process(final HttpServerExchange exchange, final Method method);
}
