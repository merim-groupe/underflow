package com.merim.digitalpayment.underflow.server;

import com.merim.digitalpayment.underflow.app.Application;
import lombok.NonNull;
import org.slf4j.LoggerFactory;

/**
 * UnderflowApplication.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
public interface UnderflowApplication {

    /**
     * Run.
     *
     * @param underflowApplication the underflow application
     * @param args                 the args
     */
    static void run(@NonNull final Class<? extends UnderflowApplication> underflowApplication,
                    @NonNull final String[] args) {
        final UnderflowApplication application;

        try {
            application = underflowApplication.getDeclaredConstructor().newInstance();
        } catch (final NoSuchMethodException e) {
            LoggerFactory.getLogger(UnderflowApplication.class)
                    .error("Failed to create instance of the class {}. Default constructor is missing.", underflowApplication.getName(), e);
            return;
        } catch (final Exception e) {
            LoggerFactory.getLogger(UnderflowApplication.class)
                    .error("An error occurred while instantiating the class {}.", underflowApplication.getName(), e);
            return;
        }

        UnderflowApplication.run(application, args);
    }

    /**
     * Run.
     *
     * @param application the application
     * @param args        the args
     */
    static void run(@NonNull final UnderflowApplication application,
                    @NonNull final String[] args) {
        Application.initMode(application.getClass());
        final UnderflowServer server;

        try {
            application.initialize(args);
        } catch (final Exception e) {
            LoggerFactory.getLogger(UnderflowApplication.class)
                    .error("An error occurred while initializing the application.", e);
            return;
        }

        try {
            final UnderflowServerBuilder builder = application.createServerBuilder();
            server = builder.build();
            application.onServerCreated(server);
        } catch (final Exception e) {
            LoggerFactory.getLogger(UnderflowApplication.class)
                    .error("An error occurred while creating the server.", e);
            return;
        }

        try {
            final MainThreadLogic mainThreadLogic = application.mainThreadLogic();
            application.onServerStart(server);
            if (mainThreadLogic == null) {
                server.startAndWait();
            } else {
                server.start();
                mainThreadLogic.accept(server);
            }
        } catch (final Exception e) {
            LoggerFactory.getLogger(UnderflowApplication.class)
                    .error("Stopping underflow server due to exception in the main thread logic...", e);
        } finally {
            server.stop();
        }

        try {
            server.waitForExit();
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize.
     *
     * @param args the args
     */
    default void initialize(final String[] args) {
    }

    /**
     * Provide a server builder for the application.
     *
     * @return the underflow server
     */
    UnderflowServerBuilder createServerBuilder();

    /**
     * Callback called just after the server being build from the builder.
     *
     * @param server the server
     */
    default void onServerCreated(final UnderflowServer server) {
    }

    /**
     * Callback called prior to the server being started.
     *
     * @param server the server
     */
    default void onServerStart(final UnderflowServer server) {
    }

    /**
     * Provide a main thread logic.
     * If this method is implemented you must be blocking while the server is running.
     * One way of achieving this is to call UnderflowServer.waitForExit().
     * Exiting the main thread logic will lead to the web server shutting down.
     *
     * @return the consumer
     */
    default MainThreadLogic mainThreadLogic() {
        return null;
    }

    /**
     * The interface Main thread logic.
     */
    interface MainThreadLogic {

        /**
         * Accept.
         *
         * @param server the server
         * @throws Exception the exception
         */
        void accept(UnderflowServer server) throws Exception;
    }
}
