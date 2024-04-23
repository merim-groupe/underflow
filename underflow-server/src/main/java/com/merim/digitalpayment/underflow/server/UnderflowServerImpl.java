package com.merim.digitalpayment.underflow.server;

import com.merim.digitalpayment.underflow.server.options.UnderflowOption;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * UnderflowServer is a standardized implementation of an Undertow server.
 * The goal is to provide a standardized way of building a web server with
 * fully furnished features such as graceful shutdown.
 *
 * @author Pierre Adam
 * @since 22.09.27
 */
@Slf4j
class UnderflowServerImpl implements UnderflowServer {

    /**
     * The Shutdown.
     */
    private final Object stopWaitLock = new Object();

    /**
     * The Path handler.
     */
    private final PathHandler pathHandler;

    /**
     * The Shutdown hooks.
     */
    private final List<Runnable> shutdownHooks;

    /**
     * The Port.
     */
    @Getter
    private final int port;

    /**
     * The Host.
     */
    @Getter
    private final String host;

    /**
     * The Shutdown handler.
     */
    private GracefulShutdownHandler shutdownHandler;

    /**
     * The Server.
     */
    private Undertow server;

    /**
     * The Waiting for exit.
     */
    private boolean waitingForExit;

    /**
     * Instantiates a new Underflow server.
     *
     * @param host     the host
     * @param port     the port
     * @param handlers the handlers
     */
    public UnderflowServerImpl(@NonNull final String host,
                               final int port,
                               @NonNull final Map<String, HandlerData> handlers) {
        this(host, port, handlers, List.of());
    }

    /**
     * Instantiates a new Underflow server.
     *
     * @param host          the host
     * @param port          the port
     * @param handlers      the handlers
     * @param shutdownHooks the shutdown hooks
     */
    public UnderflowServerImpl(@NonNull final String host,
                               final int port,
                               @NonNull final Map<String, HandlerData> handlers,
                               @NonNull final List<Runnable> shutdownHooks) {
        this.host = host;
        this.port = port;
        this.pathHandler = Handlers.path();
        this.waitingForExit = false;
        this.shutdownHooks = shutdownHooks;

        // Process the handlers.
        handlers.forEach((originalPath, handlerData) -> {
            String path = originalPath;
            HttpHandler handler = handlerData.getHandler();
            for (final UnderflowOption option : handlerData.getOptions()) {
                final String prevPath = path;
                final HttpHandler prevHandler = handler;
                path = option.alterPath(prevPath, prevHandler);
                handler = option.alterHandler(prevPath, prevHandler);
            }

            this.pathHandler.addPrefixPath(path, handler);
        });
    }


    @Override
    public void start() {
        // Register the shutdown handling using a factory to abstract the version of Java.
        ShutdownHandlingFactory.get().accept(this);

        if (this.server == null) {
            // Server doesn't exists yet. Creating and starting
            this.shutdownHandler = new GracefulShutdownHandler(this.pathHandler);
            this.server = Undertow.builder()
                    .addHttpListener(this.port, this.host)
                    .setIoThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2, 2))
                    .setWorkerThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2 * 8, 16))
                    .setHandler(this.shutdownHandler)
                    .build();

            this.server.start();
        }
    }

    @Override
    public void stop() {
        synchronized (this.stopWaitLock) {
            if (this.waitingForExit) {
                // Asynchronous way since waitForExit is currently waiting.
                // Delegate the closure of the server to waiting thread.
                this.stopWaitLock.notifyAll();
            } else {
                // Synchronous closure of the server.
                this.stopServer();
            }
        }
    }

    @Override
    public void waitForExit() throws InterruptedException {
        try {
            synchronized (this.stopWaitLock) {
                this.waitingForExit = true;
                this.stopWaitLock.wait();
                this.waitingForExit = false;
                UnderflowServerImpl.logger.debug("Stopping server from trigger.");
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
                    UnderflowServerImpl.logger.error("Failed to shutdown web server !");
                }
            }).start());
        }
    }
}
