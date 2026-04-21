package com.merim.digitalpayment.underflow.sample;

import com.merim.digitalpayment.underflow.handlers.flows.FlowApiHandler;
import com.merim.digitalpayment.underflow.results.Result;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * RoutingConflict2TestHandler.
 *
 * @author Pierre Adam
 * @since 22.02.24
 */
@Path("/conflict/{name}/bar")
public class RoutingConflict2TestHandler extends FlowApiHandler {

    /**
     * Hello result.
     *
     * @param name the name
     * @return the result
     */
    @Operation(hidden = true)
    @GET
    @Path("/")
    public Result hello(@PathParam("name") final String name) {
        return this.ok("Hello from bar " + name);
    }
}



