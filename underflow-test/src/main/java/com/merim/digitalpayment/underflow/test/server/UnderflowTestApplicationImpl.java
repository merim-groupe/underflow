package com.merim.digitalpayment.underflow.test.server;

import com.merim.digitalpayment.underflow.server.UnderflowApplication;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import lombok.NonNull;

/**
 * UnderflowTestApplicationImpl.
 *
 * @param <A> the type parameter
 * @author Pierre Adam
 * @since 24.04.22
 */
public class UnderflowTestApplicationImpl<A extends UnderflowApplication> implements UnderflowTestApplication {

    /**
     * The Application class.
     */
    private final Class<A> applicationClass;

    /**
     * The Application.
     */
    private A application;

    /**
     * Instantiates a new Underflow test application.
     *
     * @param underflowApplicationClass the underflow application class
     */
    public UnderflowTestApplicationImpl(@NonNull final Class<A> underflowApplicationClass) {
        this.applicationClass = underflowApplicationClass;
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
     * Argument provider collection.
     * You receive as parameter values from @UnderflowStartupArgs
     *
     * @param startupArgsFromAnnotation the startup args from annotation
     * @return the collection
     */
    public String[] argumentProvider(final String[] startupArgsFromAnnotation) {
        return startupArgsFromAnnotation != null ? startupArgsFromAnnotation : new String[0];
    }

    /**
     * Gets application.
     *
     * @param startupArgs the startup args
     * @return the application
     */
    public A getApplication(final String[] startupArgs) {
        if (this.application == null) {
            // Lazy load of the application
            this.application = this.createApplication();
            this.application.initialize(this.argumentProvider(startupArgs));
        }

        return this.application;
    }

    @Override
    public UnderflowServerBuilder getUnderflowServerBuilder() {
        if (this.application == null) {
            throw new RuntimeException("Invalid life cycle. Please call getApplication first to initialize the application.");
        }
        return this.application.createServerBuilder();
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
