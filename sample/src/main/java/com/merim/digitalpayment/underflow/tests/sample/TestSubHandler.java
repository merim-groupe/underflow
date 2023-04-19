package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.annotation.method.GET;
import com.merim.digitalpayment.underflow.annotation.routing.Named;
import com.merim.digitalpayment.underflow.annotation.routing.Path;
import com.merim.digitalpayment.underflow.annotation.routing.RecallNamed;
import com.merim.digitalpayment.underflow.handlers.flows.FlowApiHandler;
import com.merim.digitalpayment.underflow.results.Result;

/**
 * TestSubHandler.
 *
 * @author Pierre Adam
 * @since 23.04.19
 */
@RecallNamed(value = "value", failOnMissing = true)
@RecallNamed(value = "test", failOnMissing = true)
public class TestSubHandler extends FlowApiHandler {

    /**
     * Instantiates a new Test sub handler.
     */
    public TestSubHandler() {
    }

    /**
     * Gets home.
     *
     * @param value the value
     * @return the home
     */
    @GET
    @Path("/")
    public Result getHome(@Named("value") final Long value) {
        return this.ok("I have the value " + value + " from the parent handler");
    }
}
