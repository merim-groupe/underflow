package com.merim.digitalpayment.underflow.results;

import io.undertow.server.HttpServerExchange;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.lang.reflect.Method;

/**
 * Result.
 *
 * @author Pierre Adam
 * @since 22.07.18
 */
@Schema(description = "This is a generic class describing a result from the server. The result can be anything. HTML, Json, Yaml, etc. " +
        "To know more about what is being returned, please refer to the actually description of the call.")
public interface Result {

    /**
     * Process the result.
     *
     * @param exchange the exchange
     * @param method   the method
     */
    void process(final HttpServerExchange exchange, final Method method);
}
