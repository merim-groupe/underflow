package com.merim.digitalpayment.underflow.server;

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
    static UnderflowServerBuilder builder(final String host, final int port) {
        return new UnderflowServerBuilder(host, port);
    }

    /**
     * Gets port.
     *
     * @return the port
     */
    int getPort();

    /**
     * Get port string.
     *
     * @return the string
     */
    String getHost();

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
