package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.handlers.flows.FlowTemplateHandler;
import com.merim.digitalpayment.underflow.results.Result;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * The Sub test handler.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class PathPrefixHandler extends FlowTemplateHandler {

    /**
     * Instantiates a new Route test handler.
     */
    public PathPrefixHandler() {
        super("/templates");
    }

    /**
     * GET example with parameters in the query string.
     *
     * @return the result
     */
    @GET
    @Path("/")
    public Result home() {
        return this.ok("FOO");
    }

    /**
     * GET example with parameters in the query string.
     *
     * @return the result
     */
    @GET
    @Path("/bar")
    public Result bar() {
        return this.ok("FOO/BAR");
    }
}
