package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.results.http.HttpResult;
import io.undertow.server.HttpServerExchange;

/**
 * DTOWrapperBuilder.
 *
 * @author Pierre Adam
 * @since 25.03.19
 */
public interface DTOWrapperBuilder {

    /**
     * Build dto wrapper.
     *
     * @param exchange  the exchange
     * @param result    the result
     * @param dataModel the data model
     * @return the dto wrapper
     */
    Object build(final HttpServerExchange exchange, final HttpResult result, final Object dataModel);
}
