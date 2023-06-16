package com.merim.digitalpayment.underflow.server;

import com.merim.digitalpayment.underflow.handlers.http.RequestLoggerHandler;
import com.merim.digitalpayment.underflow.server.options.UnderflowOption;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
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
     * The Underlying handlers.
     */
    private final Map<String, HandlerData> handlers;

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
     * The Use logger handler.
     */
    private boolean useLoggerHandler;

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
        this.handlers = new HashMap<>();
        this.pathHandler = Handlers.path();
        this.shutdownHandler = new GracefulShutdownHandler(this.pathHandler);
        this.shutdownHooks = new ArrayList<>();
        this.shutdownSignalHandling = false;
        this.useLoggerHandler = false;
        this.builder = Undertow.builder()
                .setIoThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2, 2))
                .setWorkerThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2 * 8, 16));
    }

    @Override
    public UnderflowServerImpl addPrefixPath(final String prefix, final HttpHandler handler, final UnderflowOption... options) {
        this.handlers.put(prefix, new HandlerData(handler, options));
        return this;
    }

    @Override
    public UnderflowServer withRequestLogger(final boolean enable) {
        this.useLoggerHandler = enable;
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

        this.handlers.forEach((path, handlerData) -> {
            if ((this.useLoggerHandler || handlerData.hasOption(UnderflowOption.WITH_REQUEST_LOGGER)) &&
                    !handlerData.hasOption(UnderflowOption.WITHOUT_REQUEST_LOGGER)) {
                this.pathHandler.addPrefixPath(path, new RequestLoggerHandler(handlerData.getHandler()));
            } else {
                this.pathHandler.addPrefixPath(path, handlerData.getHandler());
            }
        });

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

    /**
     * The type Handler data.
     */
    private static class HandlerData {

        /**
         * The Handler.
         */
        private final HttpHandler handler;

        /**
         * The Options.
         */
        private final Set<UnderflowOption> options;

        /**
         * Instantiates a new Handler data.
         *
         * @param handler the handler
         * @param options the options
         */
        public HandlerData(final HttpHandler handler, final UnderflowOption... options) {
            this.handler = handler;
            this.options = new HashSet<>(Arrays.asList(options));
        }

        /**
         * Gets handler.
         *
         * @return the handler
         */
        public HttpHandler getHandler() {
            return this.handler;
        }

        /**
         * Gets options.
         *
         * @return the options
         */
        public Collection<UnderflowOption> getOptions() {
            return this.options;
        }

        /**
         * Has option boolean.
         *
         * @param option the option
         * @return the boolean
         */
        public boolean hasOption(final UnderflowOption option) {
            return this.options.contains(option);
        }
    }
}
