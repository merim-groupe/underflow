package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.i18n.I18n;
import com.merim.digitalpayment.underflow.i18n.cookie.I18nCookie;
import com.merim.digitalpayment.underflow.i18n.sources.PropertiesSource;
import com.merim.digitalpayment.underflow.openapi.OpenApiServerModule;
import com.merim.digitalpayment.underflow.server.UnderflowApplication;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import com.merim.digitalpayment.underflow.server.options.UnderflowCORSOption;
import com.merim.digitalpayment.underflow.server.options.UnderflowLoggerOption;
import jakarta.ws.rs.ApplicationPath;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.extensions.Extension;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import java.io.IOException;
import java.util.Locale;

/**
 * The type Main sample.
 */
@Slf4j
// TODO : Check how to handle ApplicationPath in the routing.
@ApplicationPath("/") // This is only used for OpenAPI ! The server itself wont use it for now.
@OpenAPIDefinition(info = @Info(title = "Underflow sample", version = "1.0", description = "GTFO",
        termsOfService = "Dafuk you expect ??? it's a fucking sample ! Fuck off mate...",
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
        for (final String arg : args) {
            MainSample.logger.info("Starting with argument : {}", arg);
        }

        I18nCookie.setDefaultLocale(Locale.ENGLISH);
        I18nCookie.setCookieName("UnderflowLang");

        Application.register(I18n.class, new I18n()
                .addI18nSource(PropertiesSource.builder()
                        .addLocale(Locale.FRENCH,
                                PropertiesSource.loadPropertiesFromResource(MainSample.class, "./sample.fr.properties").orElseThrow(() -> new RuntimeException("Unable to find sample.fr.properties")))
                        .addLocale(Locale.ENGLISH,
                                PropertiesSource.loadPropertiesFromResource(MainSample.class, "./sample.en.properties").orElseThrow(() -> new RuntimeException("Unable to find sample.en.properties")))
                        .addLocale(new Locale("cz"),
                                PropertiesSource.loadPropertiesFromResource(MainSample.class, "./sample.cz.properties").orElseThrow(() -> new RuntimeException("Unable to find sample.cz.properties")))
                        .build()
                )
        );
    }

    @Override
    public UnderflowServerBuilder createServerBuilder() {
        final ServerEventTestHandler serverEventTestHandler = new ServerEventTestHandler();

        return UnderflowServer.builder("0.0.0.0", 8080)
                .addModule(new OpenApiServerModule()) // Ignore error here. It's only in the sample.
                .addHandler(new SampleAssetHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(new RoutingConflict1TestHandler())
                .addHandler(new RoutingConflict2TestHandler())
                .addHandler(new RouteTestHandler(), UnderflowCORSOption.enableEasyCORS(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(serverEventTestHandler, UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(new ApiTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(new CrudApiTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler(new HomeHandler(), UnderflowCORSOption.enableEasyCORS(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addShutdownHook(() -> System.out.println("Shutting down server !"))
                .addShutdownHook(() -> {
                    MainSample.logger.error("Number of connections: {}", serverEventTestHandler.getSseh().getConnections().size());
                    serverEventTestHandler.getSseh().getConnections().forEach(serverSentEventConnection -> {
                        try {
                            serverSentEventConnection.close();
                        } catch (final IOException e) {
                            MainSample.logger.error("Error closing connection: {}", e.getMessage());
                        }
                    });
                });
    }

    @Override
    public void onServerCreated(final UnderflowServer server) {
        Application.register(UnderflowServer.class, server);
    }
}
