package com.merim.digitalpayment.underflow.test.server;

import com.merim.digitalpayment.underflow.server.UnderflowApplication;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import lombok.NonNull;

/**
 * UnderflowTestServer.
 *
 * @param <A> the type parameter
 * @author Pierre Adam
 * @since 24.04.22
 */
public class UnderflowTestApplication<A extends UnderflowApplication> implements UnderflowTestServer {

    /**
     * The Application class.
     */
    private final Class<A> applicationClass;

    /**
     * The Args.
     */
    private final String[] args;

    /**
     * The Application.
     */
    private A application;

    /**
     * Instantiates a new Underflow test application.
     *
     * @param underflowApplicationClass the underflow application class
     */
    public UnderflowTestApplication(@NonNull final Class<A> underflowApplicationClass) {
        this(underflowApplicationClass, new String[0]);
    }

    /**
     * Instantiates a new Underflow test application.
     *
     * @param underflowApplicationClass the underflow application class
     * @param args                      the args
     */
    public UnderflowTestApplication(@NonNull final Class<A> underflowApplicationClass,
                                    @NonNull final String[] args) {
        this.applicationClass = underflowApplicationClass;
        this.args = args;
        this.application = null;
    }

    /**
     * Create application underflow application.
     *
     * @return the underflow application
     */
    public A createApplication() {
        try {
            return this.applicationClass.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            throw new RuntimeException(String.format("An error occurred while instantiating underflow application %s.",
                    this.applicationClass.getName()), e);
        }
    }

    /**
     * Gets application.
     *
     * @return the application
     */
    public A getApplication() {
        if (this.application == null) {
            // Lazy load of the application
            this.application = this.createApplication();
            this.application.initialize(this.args);
        }

        return this.application;
    }

    @Override
    public UnderflowServerBuilder getUnderflowServerBuilder() {
        return this.getApplication().createServerBuilder();
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
