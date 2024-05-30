package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.handlers.flows.FlowAssetsHandler;
import com.merim.digitalpayment.underflow.server.UnderflowApplication;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import com.merim.digitalpayment.underflow.server.options.UnderflowCORSOption;
import com.merim.digitalpayment.underflow.server.options.UnderflowLoggerOption;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.openapi.api.OpenApiConfig;
import io.smallrye.openapi.api.OpenApiConfigImpl;
import io.smallrye.openapi.jaxrs.JaxRsAnnotationScanner;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import io.smallrye.openapi.runtime.scanner.OpenApiAnnotationScanner;
import io.smallrye.openapi.runtime.scanner.spi.AnnotationScanner;
import jakarta.ws.rs.ApplicationPath;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.extensions.Extension;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.jboss.jandex.Index;
import org.jboss.jandex.Indexer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * MainTest.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@NoArgsConstructor
@Slf4j
@ApplicationPath("/")
@OpenAPIDefinition(info = @Info(title = "Underflow sample", version = "1.0", description = "FUCK YOU", termsOfService = "DO NOT USE FUCKING IDIOT !",
        extensions = {
                @Extension(name = "GNARF", value = "FOO BAR"),
                @Extension(name = "object", value = "{\"foo\": \"bar\", \"foo2\": \"bar2\"}", parseValue = true),
                @Extension(name = "array", value = "[1, 2, 3, 4, \"foobar\"]", parseValue = true),
                @Extension(name = "parsedNumber", value = "1", parseValue = true),
                @Extension(name = "parsedBoolean", value = "true", parseValue = true),
                @Extension(name = "nonParsedNumber", value = "1", parseValue = false),
                @Extension(name = "nonParsedBoolean", value = "true", parseValue = false)
        }
))
public class MainSample extends jakarta.ws.rs.core.Application implements UnderflowApplication {

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        UnderflowApplication.run(MainSample.class, args);
    }

    @Override
    public void initialize(final String[] args) {
        final Indexer indexer = new Indexer();
        final Index index;

        try (final InputStream stream = this.getClass().getClassLoader()
                .getResourceAsStream(MainSample.class.getName().replace(".", "/") + ".class")) {
            indexer.index(stream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
//        try (final InputStream stream = this.getClass().getClassLoader()
//                .getResourceAsStream(HomeHandler.class.getName().replace(".", "/") + ".class")) {
//            indexer.index(stream);
//        } catch (final IOException e) {
//            throw new RuntimeException(e);
//        }
        try (final InputStream stream = this.getClass().getClassLoader()
                .getResourceAsStream(SampleAssetHandler.class.getName().replace(".", "/") + ".class")) {
            indexer.index(stream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        try (final InputStream stream = this.getClass().getClassLoader()
                .getResourceAsStream(FlowAssetsHandler.class.getName().replace(".", "/") + ".class")) {
            indexer.index(stream);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        index = indexer.complete();

        final List<AnnotationScanner> scanners = new ArrayList<>();
        scanners.add(new JaxRsAnnotationScanner());
        final OpenApiConfig config = new OpenApiConfigImpl(new SmallRyeConfigBuilder().build());
        final OpenApiAnnotationScanner scanner = new OpenApiAnnotationScanner(config, MainSample.class.getClassLoader(), index,
                () -> scanners, new ArrayList<>());
        final OpenAPI oai = scanner.scan();  //this is the OpenAPI model

        // This is your OpenAPI specification as a String
        try {
            final String openAPISpec = OpenApiSerializer.serialize(oai, Format.YAML);
            System.out.println("OpenAPI Spec");
            System.out.println(openAPISpec);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }


//        final OpenApiBuilder builder = new OpenApiBuilder(this, new HashMap<>());
//
//        final OpenAPI openAPI = builder.build();
//
//        final YAMLMapper mapper = new YAMLMapper();
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//
//        try {
//            final String openAPIStr = mapper.writeValueAsString(openAPI);
//            System.out.println(openAPIStr);
//        } catch (final JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//
//        System.exit(0);
    }

    @Override
    public UnderflowServerBuilder createServerBuilder() {
        return UnderflowServer.builder("localhost", 8080)
                .addHandler(new SampleAssetHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(new RouteTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(new ServerEventTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(new ApiTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
//                .addHandler("/api/CORS", new CORSHandler(new ApiTestHandler()), UnderflowLoggerOption.LOG_ALL_QUERY)
//                .addHandler("/api/CORSLegacy", new IDontCareAboutCORSPleaseHelpHandler(new ApiTestHandler(), true), UnderflowLoggerOption.LOG_ALL_QUERY)
//                .addHandler("/prefix", new PathPrefixHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(new HomeHandler(), UnderflowCORSOption.enableEasyCORS(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addShutdownHook(() -> System.out.println("Shutting down server !"));
    }

    @Override
    public void onServerCreated(final UnderflowServer server) {
        Application.register(UnderflowServer.class, server);
    }
}
