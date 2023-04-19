package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.annotation.method.ALL;
import com.merim.digitalpayment.underflow.annotation.routing.Path;
import com.merim.digitalpayment.underflow.results.Result;

/**
 * UniqueResponseHandler.
 *
 * @author Pierre Adam
 * @since 23.04.19
 */
public class UniqueResponseHandler extends FlowHandler {

    /**
     * The Response.
     */
    private final Result response;

    /**
     * Instantiates a new Unique response handler.
     *
     * @param response the response
     */
    public UniqueResponseHandler(final Result response) {
        this.response = response;
    }

    /**
     * Gets response.
     *
     * @return the response
     */
    @ALL
    @Path(value = "", lazyMatch = true)
    public Result getResponse() {
        return this.response;
    }
}
