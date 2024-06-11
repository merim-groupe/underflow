package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
import com.merim.digitalpayment.underflow.results.Result;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
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
     * The Ui flavor.
     */
    private final OpenApiUiFlavor uiFlavor;

    /**
     * Instantiates a new Open api handler.
     *
     * @param uiFlavor        the ui flavor
     * @param openAPISupplier the open api supplier
     */
    public OpenApiHandler(final OpenApiUiFlavor uiFlavor,
                          final Supplier<OpenAPI> openAPISupplier) {
        this.uiFlavor = uiFlavor;
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
    public String getUiDocumentation() {
        switch (this.uiFlavor) {
            case STOPLIGHT:
                return this.stoplightDocumentation();
            case REDOC:
                return this.redocDocumentation();
            case SWAGGER_UI:
                return this.swaggerUiDocumentation();
            default:
                throw new NotFoundException();
        }
    }

    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/stoplight")
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

    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/redoc")
    public String redocDocumentation() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <title>Redoc</title>\n" +
                "    <!-- needed for adaptive design -->\n" +
                "    <meta charset=\"utf-8\"/>\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "    <link href=\"https://fonts.googleapis.com/css?family=Montserrat:300,400,700|Roboto:300,400,700\" rel=\"stylesheet\">\n" +
                "\n" +
                "    <!--\n" +
                "    Redoc doesn't change outer page styles\n" +
                "    -->\n" +
                "    <style>\n" +
                "      body {\n" +
                "        margin: 0;\n" +
                "        padding: 0;\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <redoc spec-url='/docs/openapi.yaml'></redoc>\n" +
                "    <script src=\"https://cdn.redoc.ly/redoc/latest/bundles/redoc.standalone.js\"> </script>\n" +
                "  </body>\n" +
                "</html>";
    }

    /**
     * Swagger documentation string.
     *
     * @return the string
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/swagger-ui")
    public String swaggerUiDocumentation() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <title>OpenAPI UI (Powered by Underflow)</title>\n" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/5.17.14/swagger-ui.css\" >\n" +
                "\n" +
                "        <link rel=\"shortcut icon\" href=\"favicon.ico\" type=\"image/x-icon\">\n" +
                "        <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\">\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "        <div id=\"swagger-ui\"></div>\n" +
                "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/5.17.14/swagger-ui-bundle.js\" charset=\"UTF-8\"></script>\n" +
                "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/5.17.14/swagger-ui-standalone-preset.js\" charset=\"UTF-8\"> </script>\n" +
                "        <script>\n" +
                "\n" +
                "            window.onload = function() {\n" +
                "                var ui = SwaggerUIBundle({\n" +
                "                            url: '/docs/openapi.yaml',\n" +
                "                            dom_id: '#swagger-ui',\n" +
                "                            deepLinking: true,\n" +
                "                            persistAuthorization: true,\n" +
                "                            presets: [SwaggerUIBundle.presets.apis],\n" +
                "                            plugins: [SwaggerUIBundle.plugins.DownloadUrl],\n" +
                "                          })\n" +
                "            }\n" +
                "        </script>\n" +
                "    </body>\n" +
                "</html>";
    }

    /**
     * Swagger documentation string.
     *
     * @return the string
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/rapidoc")
    public String rapidocDocumentation() {
        return "<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <script type=\"module\" src=\"https://unpkg.com/rapidoc/dist/rapidoc-min.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <oauth-receiver> </oauth-receiver>\n" +
                "  <rapi-doc\n" +
                "      spec-url=\"/docs/openapi.yaml\"\n" +
                "      show-header = \"false\"\n" +
                "      nav-text-color = \"#aaa\"\n" +
                "      nav-hover-text-color = \"#fff\"\n" +
                "      nav-accent-color = \"#0d6efd\"\n" +
                "      primary-color = \"#0d6efd\"\n" +
                "      schema-style = \"table\"\n" +
                "      oauth-receiver = \"docs\"\n" +
                ">\n" +
                "  </rapi-doc>\n" +
                "</body>\n" +
                "</html>";
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
