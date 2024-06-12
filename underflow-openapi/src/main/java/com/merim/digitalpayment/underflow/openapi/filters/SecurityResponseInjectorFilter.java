package com.merim.digitalpayment.underflow.openapi.filters;

import io.smallrye.openapi.api.models.ComponentsImpl;
import io.smallrye.openapi.api.models.media.ContentImpl;
import io.smallrye.openapi.api.models.media.MediaTypeImpl;
import io.smallrye.openapi.api.models.media.SchemaImpl;
import io.smallrye.openapi.api.models.responses.APIResponseImpl;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;

import java.util.HashMap;
import java.util.Map;

/**
 * SecurityResponseInjectorFilter.
 * <p>
 * Annotate your method with @SecurityRequirement(name = "XXX")
 * Create an instance `new SecurityResponseInjectorFilter("XXX")` with XXX being the same value as in @SecurityRequirement
 * Add all the response you want to automatically add to OpenAPI when the SecurityRequirement is put on a method.
 *
 * <pre>
 *     final SecurityResponseInjectorFilter myFilter = new SecurityResponseInjectorFilter("XXX");
 *
 *     myFilter.addResponse("MyUnauthorizedResponse",
 *             SecurityResponseInjectorFilter.getStandardErrorResponse("401",
 *                     "Unauthorized if the session does not exists, is invalid or has expired."));
 *     myFilter.addResponse("MyForbiddenResponse",
 *             SecurityResponseInjectorFilter.getStandardErrorResponse("403",
 *                     "Forbidden if the session is valid but cannot access the resource."));
 *
 *     final OpenApiServerModule openApiModule = new OpenApiServerModule(new DeviceSecuredResponseFilter());
 * </pre>
 *
 * @author Pierre Adam
 * @since 24.06.11
 */
@Slf4j
public class SecurityResponseInjectorFilter implements OASFilter {

    /**
     * The constant ERROR_SCHEMA.
     */
    private static final String ERROR_SCHEMA = "ServerError";

    /**
     * The Scheme.
     */
    private final String scheme;

    /**
     * The Api responses.
     */
    private final Map<String, APIResponseImpl> apiResponses;

    /**
     * Instantiates a new Secured response filter.
     *
     * @param scheme the scheme
     */
    public SecurityResponseInjectorFilter(final String scheme) {
        this.scheme = scheme;
        this.apiResponses = new HashMap<>();
    }

    /**
     * Gets standard error content.
     *
     * @return the standard error content
     */
    public static Content getStandardErrorContent() {
        return new ContentImpl()
                .addMediaType("application/json", new MediaTypeImpl()
                        .schema(new SchemaImpl()
                                .ref("#/components/schemas/" + SecurityResponseInjectorFilter.ERROR_SCHEMA)));
    }

    /**
     * Gets standard error response.
     *
     * @param responseCode the response code
     * @param description  the description
     * @return the standard error response
     */
    public static APIResponseImpl getStandardErrorResponse(final String responseCode, final String description) {
        final APIResponseImpl apiResponse = new APIResponseImpl();

        apiResponse.setResponseCode(responseCode);
        apiResponse.setDescription(description);
        apiResponse.setContent(SecurityResponseInjectorFilter.getStandardErrorContent());

        return apiResponse;
    }

    /**
     * Add response secured response filter.
     *
     * @param name     the name
     * @param response the response
     * @return the secured response filter
     */
    public SecurityResponseInjectorFilter addResponse(final String name, final APIResponseImpl response) {
        this.apiResponses.put(name, response);
        return this;
    }

    @Override
    public void filterOpenAPI(final OpenAPI openAPI) {
        if (openAPI.getComponents() == null) {
            openAPI.setComponents(new ComponentsImpl());
        }

        final Map<String, APIResponse> responses = openAPI.getComponents().getResponses() == null ? new HashMap<>() : new HashMap<>(openAPI.getComponents().getResponses());

        for (final Map.Entry<String, APIResponseImpl> entry : this.apiResponses.entrySet()) {
            if (!responses.containsKey(entry.getKey())) {
                final APIResponse apiResponse = entry.getValue();

                responses.put(entry.getKey(), apiResponse);

                if (apiResponse.getContent() != null && apiResponse.getContent().getMediaTypes() != null) {
                    for (final MediaType mediaType : apiResponse.getContent().getMediaTypes().values()) {
                        if (mediaType.getSchema() != null
                                && mediaType.getSchema().getRef() != null
                                && mediaType.getSchema().getRef().startsWith("#/components/schemas/")) {
                            final String schemaName = mediaType.getSchema().getRef().replace("#/components/schemas/", "");
                            if (!openAPI.getComponents().getSchemas().containsKey(schemaName)) {
                                SecurityResponseInjectorFilter.logger.error("The openapi definition does not contain the referenced schema {} ! " +
                                        "The error occurred while trying to add an APIResponse {}", schemaName, entry.getKey());
                            }
                        }
                    }
                }
            }
        }

        openAPI.getComponents().setResponses(responses);
    }

    @Override
    public Operation filterOperation(final Operation operation) {
        if (operation.getSecurity() == null) {
            return operation;
        }

        for (final SecurityRequirement securityRequirement : operation.getSecurity()) {
            if (securityRequirement.getScheme(this.scheme) != null) {
                final APIResponses apiResponses = operation.getResponses();

                for (final Map.Entry<String, APIResponseImpl> entry : this.apiResponses.entrySet()) {
                    final APIResponseImpl apiResponse = entry.getValue();
                    if (apiResponses.getAPIResponse(apiResponse.getResponseCode()) == null) {
                        apiResponses.addAPIResponse(apiResponse.getResponseCode(), new APIResponseImpl().ref("#/components/responses/" + entry.getKey()));
                    }
                }
            }
        }

        return operation;
    }
}
