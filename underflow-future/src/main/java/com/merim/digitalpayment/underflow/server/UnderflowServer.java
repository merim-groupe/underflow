package com.merim.digitalpayment.underflow.server;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;

import java.util.ArrayList;
import java.util.List;
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
public class UnderflowServer {

    /**
     * The Shutdown.
     */
    private static final Object waitLock = new Object();

    /**
     * The Logger.
     */
    private final Logger logger;

    /**
     * The Path handler.
     */
    private final PathHandler pathHandler;

    /**
     * The Shutdown handler.
     */
    private final GracefulShutdownHandler shutdownHandler;

    /**
     * The Shutdown hooks.
     */
    private final List<Runnable> shutdownHooks;

    /**
     * The Builder.
     */
    private final Undertow.Builder builder;

    /**
     * The Server.
     */
    private Undertow server;

    /**
     * The Shutdown signal handling.
     */
    private boolean shutdownSignalHandling;

    /**
     * Instantiates a new Web server.
     */
    public UnderflowServer() {
        this.logger = LoggerFactory.getLogger(UnderflowServer.class);
        this.pathHandler = Handlers.path();
        this.shutdownHandler = new GracefulShutdownHandler(this.pathHandler);
        this.shutdownHooks = new ArrayList<>();
        this.shutdownSignalHandling = false;
        this.builder = Undertow.builder()
                .setIoThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2, 2))
                .setWorkerThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2 * 8, 16));
    }

    /**
     * Add prefix path web server.
     *
     * @param prefix  the prefix
     * @param handler the handler
     * @return the web server
     */
    public UnderflowServer addPrefixPath(final String prefix, final HttpHandler handler) {
        this.pathHandler.addPrefixPath(prefix, handler);
        return this;
    }

    /**
     * Add shutdown hook.
     *
     * @param closeable the closeable
     * @return the web server
     */
    public UnderflowServer addToShutdown(final AutoCloseable closeable) {
        this.shutdownHooks.add(() -> {
            try {
                closeable.close();
            } catch (final Exception e) {
                this.logger.error("An error occurred while shutting down.", e);
            }
        });
        return this;
    }

    /**
     * Add shutdown hook.
     *
     * @param hook the hook
     * @return the web server
     */
    public UnderflowServer addShutdownHook(final Runnable hook) {
        this.shutdownHooks.add(hook);
        return this;
    }

    /**
     * Add listen web server.
     *
     * @param port the port
     * @param host the host
     * @return the web server
     */
    public UnderflowServer addHttpListener(final int port, final String host) {
        this.builder.addHttpListener(port, host);
        return this;
    }

    /**
     * Alter builder web server.
     *
     * @param consumer the consumer
     * @return the web server
     */
    public UnderflowServer alterBuilder(final Consumer<Undertow.Builder> consumer) {
        consumer.accept(this.builder);
        return this;
    }

    /**
     * With shutdown handling web server.
     *
     * @return the web server
     */
    public UnderflowServer withShutdownSignalHandling() {
        this.shutdownSignalHandling = true;
        return this;
    }

    /**
     * Start the service
     */
    public void start() {
        if (this.shutdownSignalHandling) {
            Signal.handle(new Signal("TERM"), sig -> this.stop()); // Handle SIGTERM.
            Signal.handle(new Signal("INT"), sig -> this.stop()); // Handle SIGINT.
            Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
        }

        //  Create the Http Server
        this.server = this.builder
                .setHandler(this.shutdownHandler)
                .build();
        this.server.start();
    }

    /**
     * Start and wait.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void startAndWait() throws InterruptedException {
        this.start();
        this.waitForExit();
    }

    /**
     * Stop.
     */
    public void stop() {
        synchronized (UnderflowServer.waitLock) {
            UnderflowServer.waitLock.notifyAll();
            this.shutdownHandler.shutdown();
        }
    }

    /**
     * Wait for exit.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void waitForExit() throws InterruptedException {
        try {
            synchronized (UnderflowServer.waitLock) {
                UnderflowServer.waitLock.wait();
                this.logger.debug("Stopping server from trigger.");
            }
        } finally {
            this.stopServer();
        }
    }

    /**
     * Stop server.
     */
    private synchronized void stopServer() {
        if (this.server != null) {
            final Undertow serverToClose = this.server;
            this.server = null;
            this.shutdownHandler.shutdown();
            this.shutdownHandler.addShutdownListener(shutdownSuccessful -> new Thread(() -> {
                if (shutdownSuccessful) {
                    serverToClose.stop();
                    this.shutdownHooks.forEach(Runnable::run);
                } else {
                    this.logger.error("Failed to shutdown web server !");
                }
            }).start());
        }
    }
}
