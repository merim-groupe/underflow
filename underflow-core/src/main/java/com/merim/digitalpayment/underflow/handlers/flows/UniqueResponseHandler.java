package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.annotation.method.ALL;
import com.merim.digitalpayment.underflow.annotation.routing.Path;
import com.merim.digitalpayment.underflow.results.Result;

import java.util.function.Supplier;

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
    private final Supplier<Result> response;

    /**
     * Instantiates a new Unique response handler.
     *
     * @param result the result
     */
    public UniqueResponseHandler(final Result result) {
        this.response = () -> result;
    }

    /**
     * Instantiates a new Unique response handler.
     *
     * @param response the response
     */
    public UniqueResponseHandler(final Supplier<Result> response) {
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
        return this.response.get();
    }
}
