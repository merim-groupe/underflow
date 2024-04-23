package com.merim.digitalpayment.underflow.test.server;

import com.merim.digitalpayment.underflow.server.UnderflowApplication;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import lombok.NonNull;

/**
 * UnderflowTestServer.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
public class UnderflowTestApplication implements UnderflowTestServer {

    /**
     * The Application.
     */
    private final UnderflowApplication application;

    /**
     * The Application.
     */
    private final UnderflowServerBuilder serverBuilder;

    /**
     * Instantiates a new Underflow test application.
     *
     * @param underflowApplicationClass the underflow application class
     */
    public UnderflowTestApplication(@NonNull final Class<? extends UnderflowApplication> underflowApplicationClass) {
        this(underflowApplicationClass, new String[0]);
    }

    /**
     * Instantiates a new Underflow test application.
     *
     * @param underflowApplicationClass the underflow application class
     * @param args                      the args
     */
    public UnderflowTestApplication(@NonNull final Class<? extends UnderflowApplication> underflowApplicationClass,
                                    @NonNull final String[] args) {
        try {
            this.application = underflowApplicationClass.getDeclaredConstructor().newInstance();
            this.application.initialize(args);
            this.serverBuilder = this.application.createServerBuilder();
        } catch (final Exception e) {
            throw new RuntimeException(String.format("An error occurred while instantiating underflow application %s.", underflowApplicationClass.getName()), e);
        }
    }

    @Override
    public UnderflowServerBuilder getUnderflowServerBuilder() {
        return this.serverBuilder;
    }

    @Override
    public void onServerCreated(@NonNull final UnderflowServer server) {
        this.application.onServerCreated(server);
    }

    @Override
    public void onServerStart(@NonNull final UnderflowServer server) {
        this.application.onServerStart(server);
    }
}
