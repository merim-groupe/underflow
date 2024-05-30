package com.merim.digitalpayment.underflow.tests.sample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.merim.digitalpayment.underflow.api.forms.ApiForm;
import com.merim.digitalpayment.underflow.api.forms.FormError;
import com.merim.digitalpayment.underflow.handlers.flows.FlowApiHandler;
import com.merim.digitalpayment.underflow.mdc.MDCKeys;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.tests.sample.entities.ApiDescription;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.List;

/**
 * ApiTestHandler.
 *
 * @author Pierre Adam
 * @since 22.02.24
 */
@Path("/api")
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
    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class JsonBodyForm implements ApiForm {

        /**
         * The Id.
         */
        private Long id;

        /**
         * The Name.
         */
        private String name;

        @Override
        public List<FormError> isValid() {
            return null;
        }
    }
}



