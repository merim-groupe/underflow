package com.merimdigitalmedia.underflow.tests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.merimdigitalmedia.underflow.annotation.method.POST;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import com.merimdigitalmedia.underflow.api.ApiHandler;
import com.merimdigitalmedia.underflow.api.entities.ApiForm;
import com.merimdigitalmedia.underflow.api.entities.ServerError;
import com.merimdigitalmedia.underflow.mdc.MDCKeys;
import io.undertow.server.HttpServerExchange;

/**
 * ApiTestHandler.
 *
 * @author Pierre Adam
 * @since 22.02.24
 */
public class ApiTestHandler extends ApiHandler {

    /**
     * Json body.
     *
     * @param exchange the exchange
     * @throws Exception the exception
     */
    @POST
    @Path("/jsonBody")
    public void jsonBody(final HttpServerExchange exchange) throws Exception {
        this.dispatchAndBlock(exchange, () -> {
            this.getJsonBody(exchange, JsonBodyForm.class, jsonBodyForm -> {
                this.logger.info("Got the Json Body Form with:\nId   : {}\nName : {}\n=====\nRaw Json : {}",
                        jsonBodyForm.getId(), jsonBodyForm.getName(), this.getMDC(MDCKeys.Request.BODY).orElse(""));
            });
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
        public ServerError isValid() {
            return null;
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
