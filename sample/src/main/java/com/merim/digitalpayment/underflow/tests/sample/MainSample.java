package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.openapi.OpenApiApplicationModule;
import com.merim.digitalpayment.underflow.server.UnderflowApplication;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import com.merim.digitalpayment.underflow.server.modules.UnderflowApplicationModule;
import com.merim.digitalpayment.underflow.server.options.UnderflowCORSOption;
import com.merim.digitalpayment.underflow.server.options.UnderflowLoggerOption;
import io.smallrye.config.PropertiesConfigSourceProvider;
import io.smallrye.config.SmallRyeConfigBuilder;
import jakarta.ws.rs.ApplicationPath;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.extensions.Extension;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import java.util.ArrayList;
import java.util.Collection;
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
    public Collection<? extends UnderflowApplicationModule> getModules() {
        final List<UnderflowApplicationModule> modules = new ArrayList<>();

        modules.add(new OpenApiApplicationModule());

        return modules;
    }

    @Override
    public void initialize(final String[] args) {
        System.getProperties().put("my.property", "code property");

        final Config config = new SmallRyeConfigBuilder()
                .withSources(new PropertiesConfigSourceProvider("project.properties", this.getClass().getClassLoader(), true))
                .build();
        final String myProp = config.getValue("my.property", String.class);

        System.out.println(myProp);
        for (final String propertyName : config.getPropertyNames()) {
            System.out.println("- " + propertyName);
        }
        System.exit(0);
    }

    @Override
    public UnderflowServerBuilder createServerBuilder() {
        return UnderflowServer.builder("localhost", 8080)
                .addHandler(new SampleAssetHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
//                .addHandler(new RouteTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
//                .addHandler(new ServerEventTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
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
