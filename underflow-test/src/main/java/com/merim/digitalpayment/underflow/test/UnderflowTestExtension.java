package com.merim.digitalpayment.underflow.test;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.app.Mode;
import com.merim.digitalpayment.underflow.server.UnderflowApplication;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import com.merim.digitalpayment.underflow.test.server.UnderflowTestApplicationImpl;
import io.restassured.RestAssured;
import lombok.NonNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;

/**
 * UnderflowTestExtension.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
public class UnderflowTestExtension implements BeforeAllCallback, AfterAllCallback {

    /**
     * The Test application.
     */
    private UnderflowTestApplicationImpl<?> testApplication;

    /**
     * The current thread.
     */
    private UnderflowServer server;

    /**
     * Instantiates a new Underflow test extension.
     */
    public UnderflowTestExtension() {
        Application.initMode(Mode.TEST);
    }

    /**
     * Gets available port.
     *
     * @return the available port
     */
    private static int getAvailablePort() {
        try (final ServerSocket s = new ServerSocket(0)) {
            return s.getLocalPort();
        } catch (final IOException e) {
            throw new RuntimeException("Unable to find available port", e);
        }
    }

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        this.getServer(extensionContext).ifPresent(server -> {
            this.testApplication.onServerStart(server);
            server.start();
        });
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        this.getServer(extensionContext).ifPresent(server -> {
            try {
                this.testApplication.onServerStop(server);
                server.stop();
                server.waitForExit();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                Application.resetApplication();
            }
        });
    }

    /**
     * Gets custom annotation value.
     *
     * @param context the context
     * @return the custom annotation value
     */
    private Optional<UnderflowServer> getServer(@NonNull final ExtensionContext context) {
        if (this.server == null) {
            final int availablePort = UnderflowTestExtension.getAvailablePort();
            final Optional<UnderflowTest> underflowTestAnnocation = context.getElement()
                    .flatMap(element -> Optional.ofNullable(element.getAnnotation(UnderflowTest.class)));

            // Create the application.
            this.testApplication = underflowTestAnnocation
                    .map(UnderflowTest::value)
                    .map(testServerClass -> {
                        try {
                            return testServerClass.getDeclaredConstructor().newInstance();
                        } catch (final Exception e) {
                            throw new RuntimeException("Failed to create instance of the class {}. Default constructor is missing.", e);
                        }
                    })
                    .orElseThrow(() -> new RuntimeException("No test server found."));

            // Retrieve arguments from the annotations
            final String[] startupArgs = underflowTestAnnocation
                    .map(UnderflowTest::args)
                    .map(StartupArgs::value)
                    .orElse(new String[0]);

            // Initialize the application here for life cycle reasons.
            final UnderflowApplication application = this.testApplication.getApplication(startupArgs);

            // Initialize the server builder from the application
            final UnderflowServerBuilder builder = this.testApplication.getUnderflowServerBuilder()
                    .setPort(availablePort)
                    .setHost("0.0.0.0");

            // Build the server using the arguments
            this.server = builder.build(application);

            // Init again to avoid getting overridden
            Application.initMode(Mode.TEST);

            // Call the hook
            this.testApplication.onServerCreated(this.server);

            // Initialize RestAssured for user convenience
            RestAssured.port = availablePort;
            RestAssured.baseURI = "http://localhost";
        }

        return Optional.ofNullable(this.server);
    }
}
