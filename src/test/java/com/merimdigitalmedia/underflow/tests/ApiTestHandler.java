package com.merimdigitalmedia.underflow.tests;

//import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.merimdigitalmedia.underflow.annotation.method.POST;
//import com.merimdigitalmedia.underflow.annotation.routing.Path;
//import com.merimdigitalmedia.underflow.api.ApiHandler;
//import com.merimdigitalmedia.underflow.api.entities.ApiForm;
//import com.merimdigitalmedia.underflow.api.entities.ServerError;
//import com.merimdigitalmedia.underflow.mdc.MDCKeys;
//import io.undertow.server.HttpServerExchange;
//

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.merimdigitalmedia.underflow.annotation.io.Dispatch;
import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.method.POST;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import com.merimdigitalmedia.underflow.api.ApiForm;
import com.merimdigitalmedia.underflow.api.ServerError;
import com.merimdigitalmedia.underflow.handlers.flows.FlowApiHandler;
import com.merimdigitalmedia.underflow.mdc.MDCKeys;
import com.merimdigitalmedia.underflow.results.Result;
import com.merimdigitalmedia.underflow.tests.entities.ApiDescription;
import io.undertow.server.HttpServerExchange;

import java.util.Optional;

/**
 * ApiTestHandler.
 *
 * @author Pierre Adam
 * @since 22.02.24
 */
public class ApiTestHandler extends FlowApiHandler {

    /**
     * Api home result.
     *
     * @return the result
     */
    @GET
    @Path("/")
    @Dispatch
    public Result apiHome() {
        return this.ok(this.toJsonNode(ApiDescription.TEST_INSTANCE));
    }

    /**
     * Json body.
     *
     * @param exchange the exchange
     * @return the result
     */
    @POST
    @Path("/jsonbody")
    public Result jsonBody(final HttpServerExchange exchange) {
        return this.getJsonBody(exchange, JsonBodyForm.class, jsonBodyForm -> {
            this.logger.info("Got the Json Body Form with:\nId   : {}\nName : {}\n=====\nRaw Json : {}",
                    jsonBodyForm.getId(), jsonBodyForm.getName(), this.getMDC(MDCKeys.Request.BODY).orElse(""));
            return this.ok(this.toJsonNode(jsonBodyForm));
        });
    }

    /**
     * The type Json body form.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private final static class JsonBodyForm implements ApiForm {

        /**
         * The Id.
         */
        private Long id;

        /**
         * The Name.
         */
        private String name;

        @Override
        public Optional<ServerError> isValid() {
            return Optional.empty();
        }

        /**
         * Gets id.
         *
         * @return the id
         */
        public Long getId() {
            return this.id;
        }

        /**
         * Sets id.
         *
         * @param id the id
         */
        public void setId(final Long id) {
            this.id = id;
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * Sets name.
         *
         * @param name the name
         */
        public void setName(final String name) {
            this.name = name;
        }
    }
}



