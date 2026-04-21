package com.merim.digitalpayment.underflow.openapi.filters;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.OASFactory;
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
    private final Map<String, ResponseWithCode> apiResponses;

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
    public static Content getStandardJsonErrorContent() {
        return OASFactory.createContent()
                .addMediaType("application/json", OASFactory.createMediaType()
                        .schema(OASFactory.createSchema()
                                .ref("#/components/schemas/" + SecurityResponseInjectorFilter.ERROR_SCHEMA)));
    }

    /**
     * Gets standard error response.
     *
     * @param description the description
     * @return the standard error response
     */
    public static APIResponse getStandardJsonErrorResponse(final String description) {
        final APIResponse apiResponse = OASFactory.createAPIResponse();

        apiResponse.setDescription(description);
        apiResponse.setContent(SecurityResponseInjectorFilter.getStandardJsonErrorContent());

        return apiResponse;
    }

    /**
     * Add response secured response filter.
     *
     * @param name     the name
     * @param code     the code
     * @param response the response
     * @return the secured response filter
     */
    public SecurityResponseInjectorFilter addResponse(final String name, final int code, final APIResponse response) {
        final APIResponse referenceResponse = OASFactory.createAPIResponse().ref("#/components/responses/" + name);
        this.apiResponses.put(name, new ResponseWithCode(code, response, referenceResponse));
        return this;
    }

    @Override
    public void filterOpenAPI(final OpenAPI openAPI) {
        if (openAPI.getComponents() == null) {
            openAPI.setComponents(OASFactory.createComponents());
        }

        final Map<String, APIResponse> responses = openAPI.getComponents().getResponses() == null ? new HashMap<>() : new HashMap<>(openAPI.getComponents().getResponses());

        for (final Map.Entry<String, ResponseWithCode> entry : this.apiResponses.entrySet()) {
            if (!responses.containsKey(entry.getKey())) {
                final APIResponse apiResponse = entry.getValue().apiResponse();

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
                final APIResponses endpointResponses = operation.getResponses();

                for (final Map.Entry<String, ResponseWithCode> entry : this.apiResponses.entrySet()) {
                    final String code = Integer.toString(entry.getValue().code());
                    if (!endpointResponses.getAPIResponses().containsKey(code)) {
                        endpointResponses.addAPIResponse(code, entry.getValue().referenceResponse());
                    }
                }
            }
        }

        return operation;
    }

    /**
     * The type Response with code.
     */
    private record ResponseWithCode(int code, APIResponse apiResponse, APIResponse referenceResponse) {
    }
}
