package com.merim.digitalpayment.underflow.server;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
class UnderflowServerImpl implements UnderflowServer {

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
    public UnderflowServerImpl() {
        this.logger = LoggerFactory.getLogger(UnderflowServerImpl.class);
        this.pathHandler = Handlers.path();
        this.shutdownHandler = new GracefulShutdownHandler(this.pathHandler);
        this.shutdownHooks = new ArrayList<>();
        this.shutdownSignalHandling = false;
        this.builder = Undertow.builder()
                .setIoThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2, 2))
                .setWorkerThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2 * 8, 16));
    }

    @Override
    public UnderflowServerImpl addPrefixPath(final String prefix, final HttpHandler handler) {
        this.pathHandler.addPrefixPath(prefix, handler);
        return this;
    }

    @Override
    public UnderflowServerImpl addShutdownHook(final Runnable hook) {
        this.shutdownHooks.add(hook);
        return this;
    }

    @Override
    public UnderflowServerImpl addHttpListener(final int port, final String host) {
        this.builder.addHttpListener(port, host);
        return this;
    }

    @Override
    public UnderflowServerImpl alterBuilder(final Consumer<Undertow.Builder> consumer) {
        consumer.accept(this.builder);
        return this;
    }

    @Override
    public UnderflowServerImpl withShutdownSignalHandling() {
        this.shutdownSignalHandling = true;
        return this;
    }

    @Override
    public void start() {
        if (this.shutdownSignalHandling) {
            ShutdownHandlingFactory.get().accept(this);
        }

        //  Create the Http Server
        this.server = this.builder
                .setHandler(this.shutdownHandler)
                .build();
        this.server.start();
    }

    @Override
    public void stop() {
        synchronized (UnderflowServerImpl.waitLock) {
            UnderflowServerImpl.waitLock.notifyAll();
            this.shutdownHandler.shutdown();
        }
    }

    @Override
    public void waitForExit() throws InterruptedException {
        try {
            synchronized (UnderflowServerImpl.waitLock) {
                UnderflowServerImpl.waitLock.wait();
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
