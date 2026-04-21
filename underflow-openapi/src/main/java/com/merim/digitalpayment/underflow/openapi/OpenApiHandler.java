package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
import com.merim.digitalpayment.underflow.results.Result;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.IOContext;
import io.smallrye.openapi.runtime.io.JsonIO;
import io.smallrye.openapi.runtime.io.OpenAPIDefinitionIO;
import io.undertow.server.HttpServerExchange;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.servers.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
     * The Add current server.
     */
    private final boolean addCurrentServer;

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
        this.addCurrentServer = true;
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
        return switch (this.uiFlavor) {
            case STOPLIGHT -> this.stoplightDocumentation();
            case REDOC -> this.redocDocumentation();
            case SWAGGER_UI -> this.swaggerUiDocumentation();
            default -> throw new NotFoundException();
        };
    }

    /**
     * Stoplight documentation string.
     *
     * @return the string
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/stoplight")
    public String stoplightDocumentation() {
        final OpenAPI openAPI = this.openAPISupplier.get();
        final String name = openAPI.getInfo().getTitle() != null ? openAPI.getInfo().getTitle() + " " : "";

        return "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" +
                "    <title>" + name + "Documentation</title>\n" +
                "    <!-- Embed elements Elements via Web Component -->\n" +
                "    <script src=\"https://unpkg.com/@stoplight/elements/web-components.min.js\"></script>\n" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/@stoplight/elements/styles.min.css\">\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<elements-api\n" +
                "        apiDescriptionUrl=\"/docs/openapi.yaml\"\n" +
                "        router=\"hash\"\n" +
                "        layout=\"responsive\"\n" +
                "        hideSchemas=\"true\"\n" +
                "        logo=\"data:image/png;base64,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\"\n" +
                "/>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
    }

    /**
     * Redoc documentation string.
     *
     * @return the string
     */
    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("/redoc")
    public String redocDocumentation() {
        final OpenAPI openAPI = this.openAPISupplier.get();
        final String name = openAPI.getInfo().getTitle() != null ? openAPI.getInfo().getTitle() + " " : "";

        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "  <head>\n" +
                "    <title>" + name + "Documentation</title>\n" +
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
        final OpenAPI openAPI = this.openAPISupplier.get();
        final String name = openAPI.getInfo().getTitle() != null ? openAPI.getInfo().getTitle() + " " : "";

        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "    <head>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <title>" + name + "Documentation</title>\n" +
                "        <link rel=\"stylesheet\" type=\"text/css\" href=\"https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/5.29.1/swagger-ui.css\" >\n" +
                "\n" +
                "        <link rel=\"shortcut icon\" href=\"favicon.ico\" type=\"image/x-icon\">\n" +
                "        <link rel=\"icon\" href=\"favicon.ico\" type=\"image/x-icon\">\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "        <div id=\"swagger-ui\"></div>\n" +
                "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/5.29.1/swagger-ui-bundle.js\" charset=\"UTF-8\"></script>\n" +
                "        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/swagger-ui/5.29.1/swagger-ui-standalone-preset.js\" charset=\"UTF-8\"> </script>\n" +
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
        final OpenAPI openAPI = this.openAPISupplier.get();
        final String name = openAPI.getInfo().getTitle() != null ? openAPI.getInfo().getTitle() + " " : "";

        return "<!doctype html>\n" +
                "<html>\n" +
                "<head>\n" +
                "  <title>" + name + "Documentation</title>\n" +
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
     * @param exchange the exchange
     * @return the openapi description as yaml
     */
    @Operation(hidden = true)
    @Produces("application/x-yaml")
    @GET
    @Path("openapi")
    public Result serveOpenAPI(@Context final HttpServerExchange exchange) {
        return this.serveOpenAPIYaml(exchange);
    }

    /**
     * Serve the server openapi description as yaml.
     *
     * @param exchange the exchange
     * @return the openapi description as yaml
     */
    @Operation(hidden = true)
    @Produces("application/yaml")
    @GET
    @Path("openapi.yaml")
    public Result serveOpenAPIYaml(@Context final HttpServerExchange exchange) {
        try {
            return this.ok(this.serialize(this.getOpenAPI(exchange), Format.YAML))
                    .withContentType("application/yaml");
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serve the server openapi description as json.
     *
     * @param exchange the exchange
     * @return the openapi description as json
     */
    @Operation(hidden = true)
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("openapi.json")
    public Result serveOpenAPIJson(@Context final HttpServerExchange exchange) {
        try {
            return this.ok(this.serialize(this.getOpenAPI(exchange), Format.JSON))
                    .withContentType(MediaType.APPLICATION_JSON);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Serialize string.
     *
     * @param openAPI the open api
     * @param format  the format
     * @return the string
     * @throws IOException the io exception
     */
    private String serialize(final OpenAPI openAPI, final Format format) throws IOException {
        // Previously used io.smallrye.openapi.runtime.io.OpenApiSerializer.serialize(OpenAPI, Format)
        // Now deprecated without clear replacement.
        // This is basically the same implementation as OpenApiSerializer.serialize(...) which uses non-deprecated classes.
        final JsonIO<Object, Object, Object, Object, Object> jsonIO = JsonIO.newInstance(null);

        return new OpenAPIDefinitionIO<>(IOContext.forJson(jsonIO))
                .write(openAPI)
                .map(node -> jsonIO.toString(node, format))
                .orElseThrow(IOException::new);
    }

    /**
     * Gets open api.
     *
     * @param exchange the exchange
     * @return the open api
     */
    private OpenAPI getOpenAPI(final HttpServerExchange exchange) {
        final OpenAPI original = this.openAPISupplier.get();

        if (this.addCurrentServer) {
            final List<Server> servers = original.getServers() != null ? new ArrayList<>(original.getServers()) : new ArrayList<>();
            final OpenAPI openAPI = OASFactory.createOpenAPI()
                    .openapi(original.getOpenapi())
                    .info(original.getInfo())
                    .externalDocs(original.getExternalDocs())
                    .security(original.getSecurity())
                    .tags(original.getTags())
                    .paths(original.getPaths())
                    .components(original.getComponents())
                    .extensions(original.getExtensions());

            servers.add(OASFactory.createServer()
                    .description("Server")
                    .url(exchange.getRequestScheme() + "://" + exchange.getHostAndPort()));
            openAPI.servers(servers);

            return openAPI;
        } else {
            return original;
        }
    }
}
