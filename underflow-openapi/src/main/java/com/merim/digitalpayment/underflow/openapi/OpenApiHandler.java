package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
import com.merim.digitalpayment.underflow.results.Result;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.models.OpenAPI;

import java.io.IOException;
import java.util.function.Supplier;

/**
 * OpenApiHandler.
 *
 * @author Pierre Adam
 * @since 24.06.03
 */
@Path("/docs")
public class OpenApiHandler extends FlowHandler {

    /**
     * The Open api supplier.
     */
    private final Supplier<OpenAPI> openAPISupplier;

    /**
     * Instantiates a new Open api handler.
     *
     * @param openAPISupplier the open api supplier
     */
    public OpenApiHandler(final Supplier<OpenAPI> openAPISupplier) {
        this.openAPISupplier = openAPISupplier;
    }

    /**
     * Stoplight documentation string.
     *
     * @return the string
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/")
    public String stoplightDocumentation() {
        return "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "    <title>PlutusApi Documentation</title>\n" +
                "    <!-- Embed elements Elements via Web Component -->\n" +
                "    <script src=\"https://unpkg.com/@stoplight/elements/web-components.min.js\"></script>\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/@stoplight/elements/styles.min.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<elements-api\n" +
                "        apiDescriptionUrl=\"/docs/openapi.yaml\"\n" +
                "        router=\"hash\"\n" +
                "        layout=\"sidebar\"\n" +
                "/>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
    }

    /**
     * Serve the server openapi description as yaml.
     *
     * @return the openapi description as yaml
     */
    @Operation(hidden = true)
    @Produces("application/x-yaml")
    @GET
    @Path("openapi")
    public Result serveOpenAPI() {
        return this.serveOpenAPIYaml();
    }

    /**
     * Serve the server openapi description as yaml.
     *
     * @return the openapi description as yaml
     */
    @Operation(hidden = true)
    @Produces("application/x-yaml")
    @GET
    @Path("openapi.yaml")
    public Result serveOpenAPIYaml() {
        try {
            return this.ok(OpenApiSerializer.serialize(this.openAPISupplier.get(), Format.YAML));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serve the server openapi description as json.
     *
     * @return the openapi description as json
     */
    @Operation(hidden = true)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("openapi.json")
    public Result serveOpenAPIJson() {
        try {
            return this.ok(OpenApiSerializer.serialize(this.openAPISupplier.get(), Format.JSON));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
