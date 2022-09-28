package com.merim.digitalpayment.underflow.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * UnderflowServer is a standardized implementation of an Undertow server.
 * The goal is to provide a standardized way of building a web server with
 * fully furnished features such as graceful shutdown.
 *
 * @author Pierre Adam
 * @since 22.09.27
 */
@SuppressWarnings("restriction")
public interface UnderflowServer {

    /**
     * Create underflow server.
     *
     * @return the underflow server
     */
    static UnderflowServer create() {
        return new UnderflowServerImpl();
    }

    /**
     * Add prefix path web server.
     *
     * @param prefix  the prefix
     * @param handler the handler
     * @return the web server
     */
    UnderflowServer addPrefixPath(final String prefix, final HttpHandler handler);

    /**
     * Add shutdown hook.
     *
     * @param hook the hook
     * @return the web server
     */
    UnderflowServer addShutdownHook(final Runnable hook);

    /**
     * Add shutdown hook.
     *
     * @param closeable the closeable
     * @return the web server
     */
    default UnderflowServer addToShutdown(final AutoCloseable closeable) {
        this.addShutdownHook(() -> {
            try {
                closeable.close();
            } catch (final Exception e) {
                LoggerFactory.getLogger(this.getClass()).error("An error occurred while shutting down.", e);
            }
        });
        return this;
    }

    /**
     * Add listen web server.
     *
     * @param port the port
     * @param host the host
     * @return the web server
     */
    UnderflowServer addHttpListener(final int port, final String host);

    /**
     * Alter builder web server.
     *
     * @param consumer the consumer
     * @return the web server
     */
    UnderflowServer alterBuilder(final Consumer<Undertow.Builder> consumer);

    /**
     * With shutdown handling web server.
     *
     * @return the web server
     */
    UnderflowServer withShutdownSignalHandling();

    /**
     * Start the service
     */
    void start();

    /**
     * Wait for exit.
     *
     * @throws InterruptedException the interrupted exception
     */
    void waitForExit() throws InterruptedException;

    /**
     * Start and wait.
     *
     * @throws InterruptedException the interrupted exception
     */
    default void startAndWait() throws InterruptedException {
        this.start();
        this.waitForExit();
    }

    /**
     * Stop.
     */
    void stop();
}
