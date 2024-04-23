package com.merim.digitalpayment.underflow.test;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.app.Mode;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
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
     * The current thread.
     */
    private UnderflowServer testServer;

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
        this.getCustomAnnotationValue(extensionContext).ifPresent(UnderflowServer::start);
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        this.getCustomAnnotationValue(extensionContext).ifPresent(UnderflowServer::stop);
    }

    /**
     * Gets custom annotation value.
     *
     * @param context the context
     * @return the custom annotation value
     */
    private Optional<UnderflowServer> getCustomAnnotationValue(@NonNull final ExtensionContext context) {
        if (this.testServer == null) {
            final int availablePort = UnderflowTestExtension.getAvailablePort();
            this.testServer = context.getElement()
                    .flatMap(element -> Optional.ofNullable(element.getAnnotation(UnderflowTest.class)))
                    .map(UnderflowTest::value)
                    .map(testServerClass -> {
                        try {
                            return testServerClass.getDeclaredConstructor().newInstance();
                        } catch (final Exception e) {
                            throw new RuntimeException("Failed to create instance of the class {}. Default constructor is missing.", e);
                        }
                    })
                    .map(underflowTestServer -> underflowTestServer.getUnderflowServerBuilder()
                            .setPort(availablePort)
                            .setHost("0.0.0.0")
                            .build()
                    )
                    .orElse(null);

            if (this.testServer != null) {
                RestAssured.port = availablePort;
                RestAssured.baseURI = "http://localhost";
            }
        }

        return Optional.ofNullable(this.testServer);
    }
}
