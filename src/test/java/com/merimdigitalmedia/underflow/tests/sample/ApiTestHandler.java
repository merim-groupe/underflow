package com.merimdigitalmedia.underflow.tests.sample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.method.POST;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import com.merimdigitalmedia.underflow.api.ApiForm;
import com.merimdigitalmedia.underflow.api.ServerError;
import com.merimdigitalmedia.underflow.handlers.flows.FlowApiHandler;
import com.merimdigitalmedia.underflow.mdc.MDCKeys;
import com.merimdigitalmedia.underflow.results.Result;
import com.merimdigitalmedia.underflow.tests.sample.entities.ApiDescription;

import java.io.InputStream;
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
    public Result apiHome() {
        return this.ok(this.toJsonNode(ApiDescription.TEST_INSTANCE));
    }

    /**
     * Json body.
     *
     * @param bodyInputStream the body input stream
     * @return the result
     */
    @POST
    @Path("/jsonbody")
    public Result jsonBody(final InputStream bodyInputStream) {
        return this.getJsonBody(bodyInputStream, JsonBodyForm.class, jsonBodyForm -> {
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



