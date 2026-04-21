package com.merim.digitalpayment.underflow.sample;

import com.merim.digitalpayment.underflow.handlers.flows.FlowApiHandler;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.sample.entities.ApiDescription;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * ApiTestHandler.
 *
 * @author Pierre Adam
 * @since 22.02.24
 */
@Tag(name = "Basic API Sample")
@Path("/api")
public class ApiTestHandler extends FlowApiHandler {

    /**
     * Api home result.
     *
     * @return the result
     */
    @Operation(
            summary = "Json object",
            description = "The framework replies with a 200 containing an object that is being serialized to JSON."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, return list of all contracts",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ApiDescription.class))),
    })
    @GET
    @Path("/object")
    public Result apiObject() {
        return this.ok(this.toJsonNode(ApiDescription.TEST_INSTANCE));
    }

    /**
     * Api home result.
     *
     * @return the result
     */
    @Operation(
            summary = "Json list",
            description = "The framework replies with a 200 containing an list of objects that are being serialized to JSON."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, return list of all contracts",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(type = SchemaType.ARRAY, implementation = ApiDescription.class))),
    })
    @GET
    @Path("/list")
    public Result apiList() {
        final List<ApiDescription> list = new ArrayList<>();

        list.add(ApiDescription.TEST_INSTANCE);
        list.add(ApiDescription.TEST_INSTANCE);

        return this.ok(this.toJsonNode(list));
    }

    @GET
    @Path("/exception")
    public Result exception() {
        throw new RuntimeException("Sample Exception");
    }
//
//    /**
//     * Api home result.
//     *
//     * @param name the name
//     * @return the result
//     */
//    @Operation(
//            summary = "Search object",
//            description = "Sample"
//    )
//    @APIResponses({
//            @APIResponse(
//                    responseCode = "200",
//                    description = "Success, return list of all contracts",
//                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
//                            schema = @Schema(implementation = ApiDescription.class))),
//    })
//    @GET
//    @Path("/search/{name}")
//    public Result apiSearch(@PathParam("name") final String name) {
//        return this.ok(this.toJsonNode(ApiDescription.TEST_INSTANCE));
//    }
//
//    /**
//     * Test result.
//     *
//     * @param name   the name
//     * @param option the option
//     * @param uuid   the uuid
//     * @return the result
//     */
//    @GET
//    @Path("/search/{name}/{option}/{uuid}")
//    public Result test(@PathParam("name") final String name,
//                       @PathParam("option") final Integer option,
//                       @PathParam("uuid") final UUID uuid) {
//        return this.ok(this.toJsonNode(ApiDescription.TEST_INSTANCE));
//    }
//
//    /**
//     * Json body.
//     *
//     * @param bodyInputStream the body input stream
//     * @return the result
//     */
//    @POST
//    @Path("/jsonbody")
//    public Result jsonBody(final InputStream bodyInputStream) {
//        return this.getJsonBody(bodyInputStream, JsonBodyForm.class, jsonBodyForm -> {
//            this.logger.info("Got the Json Body Form with:\nId   : {}\nName : {}\n=====\nRaw Json : {}",
//                    jsonBodyForm.getId(), jsonBodyForm.getName(), this.getMDC(MDCKeys.Request.BODY).orElse(""));
//            return this.ok(this.toJsonNode(jsonBodyForm));
//        });
//    }
//
//    /**
//     * The type Json body form.
//     */
//    @Getter
//    @Setter
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    private static final class JsonBodyForm implements ApiForm {
//
//        /**
//         * The Id.
//         */
//        private Long id;
//
//        /**
//         * The Name.
//         */
//        private String name;
//
//        @Override
//        public List<FormError> isValid() {
//            return null;
//        }
//    }
}



