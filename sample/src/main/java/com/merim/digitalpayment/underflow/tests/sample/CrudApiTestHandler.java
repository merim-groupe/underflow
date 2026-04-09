package com.merim.digitalpayment.underflow.tests.sample;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.merim.digitalpayment.underflow.annotation.routing.QueryParamList;
import com.merim.digitalpayment.underflow.annotation.routing.QueryParamRequired;
import com.merim.digitalpayment.underflow.api.forms.ApiForm;
import com.merim.digitalpayment.underflow.api.forms.FormError;
import com.merim.digitalpayment.underflow.entities.ServerError;
import com.merim.digitalpayment.underflow.handlers.flows.FlowApiHandler;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.tests.sample.security.MySecurityScope;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBodySchema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CrudApiTestHandler.
 *
 * @author Pierre Adam
 * @since 22.02.24
 */
@Tag(name = "Api CRUD Sample")
@Path("/api/crud")
public class CrudApiTestHandler extends FlowApiHandler {

    /**
     * The Storage.
     */
    private final Map<Integer, TestStorageEntity> storage;

    /**
     * Instantiates a new Api test handler.
     */
    public CrudApiTestHandler() {
        this.storage = new HashMap<>();

        final TestStorageEntity default1 = new TestStorageEntity("Default entry 1");
        final TestStorageEntity default2 = new TestStorageEntity("Default entry 2");

        this.storage.put(default1.id, default1);
        this.storage.put(default2.id, default2);
    }

    /**
     * Api home result.
     *
     * @param id the id
     * @return the result
     */
    @Operation(
            summary = "Read an entry",
            description = "Get the entry representation for the given id if it exists."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, returns the object",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestStorageEntity.class))),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ServerError.class))
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}")
    public Result getKey(@PathParam("id") final Integer id) {
        if (this.storage.containsKey(id)) {
            return this.ok(this.toJsonNode(this.storage.get(id)));
        }

        return this.notFound(this.toJsonNode(new ServerError("Not Found", String.format("No entry with the id %d were found.", id))));
    }

    @Operation(
            summary = "Read an entry",
            description = "Get the entry representation for the given id if it exists."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, returns the object",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestStorageEntity.class))),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ServerError.class))
            )
    })
    @MySecurityScope("api")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{id}/secure")
    public Result getKeyProtected(@PathParam("id") final Integer id) {
        if (this.storage.containsKey(id)) {
            return this.ok(this.toJsonNode(this.storage.get(id)));
        }

        return this.notFound(this.toJsonNode(new ServerError("Not Found", String.format("No entry with the id %d were found.", id))));
    }

    /**
     * Api home result.
     *
     * @return the result
     */
    @Operation(
            summary = "Lists the entries",
            description = "Get the list of the entries."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, return list entries",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(type = SchemaType.ARRAY, implementation = TestStorageEntity.class))),
    })
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/list")
    public Result getList() {
        return this.ok(this.toJsonNode(this.storage.values()));
    }

    /**
     * Api home result.
     *
     * @param values the values
     * @return the result
     */
    @Operation(
            summary = "Weird create multiple entries",
            description = "Create an entry and returns the list of representations."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, returns the array of objects that were created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(type = SchemaType.ARRAY, implementation = TestStorageEntity.class))),
    })
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/weird-add-multiple")
    public Result weirdAddMultiple(@QueryParam("value") @QueryParamRequired @QueryParamList(String.class)
                                   @DefaultValue("default value !") final List<String> values) {
        final List<TestStorageEntity> result = new ArrayList<>();

        for (final String value : values) {
            final TestStorageEntity entity = new TestStorageEntity(value);

            this.storage.put(entity.getId(), entity);
            result.add(entity);
        }

        return this.ok(this.toJsonNode(result));
    }

    /**
     * Weird add or default result.
     *
     * @param value the value
     * @return the result
     */
    @Operation(
            summary = "Weird create an entry with given value or default one",
            description = "Create an entry and returns it's representation."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, returns the object that was created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestStorageEntity.class))),
    })
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/weird-add-or-default")
    public Result weirdAddOrDefault(@QueryParam("value") @DefaultValue("default value !") final String value) {
        final TestStorageEntity entity = new TestStorageEntity(value);

        this.storage.put(entity.getId(), entity);

        return this.ok(this.toJsonNode(entity));
    }


    /**
     * Api home result.
     *
     * @param bodyInputStream the body input stream
     * @return the result
     */
    @Operation(
            summary = "Create an entry",
            description = "Create an entry and returns it's representation."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, returns the object that was added",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestStorageEntity.class))),
    })
    @Parameter()
    @Consumes(MediaType.APPLICATION_JSON)
    @RequestBodySchema(TestStorageForm.class) // This requires the @Consumes(...) in order to work. DO NOT USE @RequestBody
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    @Path("/")
    public Result create(@Context final InputStream bodyInputStream) {
        return this.getJsonBody(bodyInputStream, TestStorageForm.class, form -> {
            final TestStorageEntity entity = new TestStorageEntity(form.getValue());

            this.storage.put(entity.getId(), entity);

            return this.getKey(entity.getId());
        });
    }

    /**
     * Update result.
     *
     * @param id              the id
     * @param bodyInputStream the body input stream
     * @return the result
     */
    @Operation(
            summary = "Create an entry",
            description = "Create an entry and returns it's representation."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Success, returns the object that was added",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestStorageEntity.class))),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ServerError.class))
            )
    })
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RequestBodySchema(TestStorageForm.class)
    @PATCH
    @Path("/{id}")
    public Result update(@PathParam("id") final Integer id,
                         @Context final InputStream bodyInputStream) {
        if (!this.storage.containsKey(id)) {
            return this.notFound(this.toJsonNode(new ServerError("Not Found", String.format("No entry with the id %d were found.", id))));
        }

        return this.getJsonBody(bodyInputStream, TestStorageForm.class, form -> {
            this.storage.get(id).setValue(form.getValue());

            return this.getKey(id);
        });
    }

    /**
     * Delete result.
     *
     * @param id the id
     * @return the result
     */
    @Operation(
            summary = "Delete an entry",
            description = "Delete an entry and respond no-content."
    )
    @APIResponses({
            @APIResponse(
                    responseCode = "204",
                    description = "Success, the object was deleted",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = TestStorageEntity.class))),
            @APIResponse(
                    responseCode = "404",
                    description = "Not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(implementation = ServerError.class))
            )
    })
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    @Path("/{id}")
    public Result delete(@PathParam("id") final Integer id) {
        if (!this.storage.containsKey(id)) {
            return this.notFound(this.toJsonNode(new ServerError("Not Found", String.format("No entry with the id %d were found.", id))));
        }

        this.storage.remove(id);

        return this.noContent();
    }

    /**
     * The type Test storage form.
     */
    @Getter
    @Setter
    @Schema(description = "Payload to create or edit an entry of the API.")
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestStorageForm implements ApiForm {

        /**
         * The Value.
         */
        @Schema(description = "The value of the entity !", required = true, example = "foo bar value !", nullable = false)
        protected String value;

        @Override
        public List<FormError> isValid() {
            final List<FormError> errors = new ArrayList<>();

            if (this.value == null || this.value.trim().isEmpty()) {
                errors.add(new FormError("value", "Is required"));
            }

            return errors;
        }
    }

    /**
     * The type Test storage entity.
     */
    @Getter
    public static class TestStorageEntity extends TestStorageForm {

        /**
         * The constant idCounter.
         */
        private static int idCounter = 1;

        /**
         * The Id.
         */
        @Schema(description = "Unique ID of the entity", example = "1", nullable = false)
        private final int id;

        /**
         * Instantiates a new Test storage entity.
         *
         * @param value the value
         */
        public TestStorageEntity(final String value) {
            this.id = TestStorageEntity.idCounter++;
            this.value = value;
        }
    }
}



