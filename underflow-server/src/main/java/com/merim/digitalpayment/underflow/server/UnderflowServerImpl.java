package com.merim.digitalpayment.underflow.server;

import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
import com.merim.digitalpayment.underflow.routing.RegexRouterHandler;
import com.merim.digitalpayment.underflow.server.modules.UnderflowServerModule;
import com.merim.digitalpayment.underflow.server.options.UnderflowOption;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import io.undertow.server.handlers.PathHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * UnderflowServer is a standardized implementation of an Undertow server.
 * The goal is to provide a standardized way of building a web server with
 * fully furnished features such as graceful shutdown.
 *
 * @author Pierre Adam
 * @since 22.09.27
 */
@Slf4j
public class UnderflowServerImpl implements UnderflowServer {

    /**
     * The Trigger shutdown thread.
     */
    private final ReentrantLock triggerShutdownThread = new ReentrantLock();

    /**
     * The Condition.
     */
    private final Condition condition = this.triggerShutdownThread.newCondition();

    /**
     * The waiting for stop lock.
     */
    private final Object waitStopLock = new Object();

    /**
     * The shutdown lock.
     */
    private final Object shutdownLock = new Object();

    /**
     * The Application.
     */
    @Getter
    private final UnderflowApplication application;

    /**
     * The Class loader.
     */
    @Getter
    private final ClassLoader applicationClassLoader;

    /**
     * The Handlers.
     */
    @Getter
    private final Map<String, List<HandlerData>> handlers;

    /**
     * The Path handler.
     */
    @Getter
    private final PathHandler pathHandler;

    /**
     * The Shutdown hooks.
     */
    private final List<Runnable> shutdownHooks;

    /**
     * The Modules.
     */
    private final Collection<UnderflowServerModule> modules;

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
    @Getter
    private Undertow server;

    /**
     * The Waiting for exit.
     */
    private boolean waitingForExit;

    /**
     * The Shutdown thread.
     */
    private Thread shutdownThread;

    /**
     * Instantiates a new Underflow server.
     *
     * @param application            the application
     * @param applicationClassLoader the class loader
     * @param host                   the host
     * @param port                   the port
     * @param handlers               the handlers
     * @param shutdownHooks          the shutdown hooks
     * @param modules                the modules
     */
    UnderflowServerImpl(@NonNull final UnderflowApplication application,
                        final ClassLoader applicationClassLoader,
                        @NonNull final String host,
                        final int port,
                        @NonNull final Map<String, List<HandlerData>> handlers,
                        @NonNull final List<Runnable> shutdownHooks,
                        final Collection<UnderflowServerModule> modules) {
        this.application = application;
        this.applicationClassLoader = applicationClassLoader != null ? applicationClassLoader : Thread.currentThread().getContextClassLoader();
        this.host = host;
        this.port = port;
        this.handlers = handlers;
        this.pathHandler = Handlers.path();
        this.waitingForExit = false;
        this.shutdownHooks = shutdownHooks;
        this.modules = modules;

        // Process the handlers.
        handlers.forEach((path, handlersData) -> {
            if (handlersData.size() == 1) {
                this.pathHandler.addPrefixPath(path, UnderflowServerImpl.createHandler(path, handlersData.get(0)));
            } else if (handlersData.size() > 1) {
                this.pathHandler.addPrefixPath(path, UnderflowServerImpl.createHandler(handlersData));
            }
        });
    }

    /**
     * Create handler http handler.
     *
     * @param handlersData the handlers data
     * @return the http handler
     */
    private static HttpHandler createHandler(final List<HandlerData> handlersData) {
        final RegexRouterHandler regexRouterHandler = new RegexRouterHandler();

        for (final HandlerData handlerData : handlersData) {
            if (!(handlerData.getHandler() instanceof FlowHandler)) {
                throw new RuntimeException("Conflicting routes with HttpHandler not extending FlowHandler is not supported !");
            }

            final FlowHandler flowHandler = (FlowHandler) handlerData.getHandler();
            final HttpHandler finalHandler = UnderflowServerImpl.createHandler(flowHandler.getHandlerInfo().getBasePath(), handlerData);

            regexRouterHandler.addPrefixPath(flowHandler.getHandlerInfo().getVariableRegexPath(), finalHandler);
        }

        return regexRouterHandler;
    }

    /**
     * Create handler http handler.
     *
     * @param path        the path
     * @param handlerData the handler data
     * @return the http handler
     */
    private static HttpHandler createHandler(final String path, final HandlerData handlerData) {
        HttpHandler handler = handlerData.getHandler();

        for (final UnderflowOption option : handlerData.getOptions()) {
            handler = option.alterHandler(path, handler);
        }

        return handler;
    }

    @Override
    public void start() {
        // Register the shutdown handling using a factory to abstract the version of Java.
        ShutdownHandlingFactory.get().accept(this);

        if (this.server == null) {
            // Server doesn't exists yet.
            if (this.shutdownThread != null) {
                // If there is a shutdown thread currently running, waiting before starting a new server.
                try {
                    this.waitForExit();
                } catch (final InterruptedException ignore) {
                }
            }

            this.shutdownHandler = new GracefulShutdownHandler(this.pathHandler);
            this.server = Undertow.builder()
                    .addHttpListener(this.port, this.host)
                    .setIoThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2, 2))
                    .setWorkerThreads(Math.max(Runtime.getRuntime().availableProcessors() * 2 * 8, 16))
                    .setHandler(this.shutdownHandler)
                    .build();

            this.server.start();
            this.modules.forEach(module -> module.onServerCreated(this));
        }
    }

    @Override
    public void stop() {
        synchronized (this.waitStopLock) {
            if (this.waitingForExit) {
                // Asynchronous way since waitForExit is currently waiting.
                // Delegate the closure of the server to waiting thread.
                this.waitStopLock.notifyAll();
            } else {
                // Synchronous closure of the server.
                this.stopServer();
            }
        }
    }

    @Override
    public void waitForExit() throws InterruptedException {
        if (this.server != null) {
            try {
                synchronized (this.waitStopLock) {
                    this.waitingForExit = true;
                    this.waitStopLock.wait();
                    this.waitingForExit = false;
                    UnderflowServerImpl.logger.debug("Stopping server from trigger.");
                }
            } finally {
                this.stopServer();
            }
        }

        synchronized (this.shutdownLock) {
            if (this.shutdownThread != null) {
                this.triggerShutdownThread.lock();

                try {
                    this.condition.await();
                } finally {
                    this.triggerShutdownThread.unlock();
                }

                this.shutdownThread.join(60_000); // Allow 60s for the hooks to complete
                if (this.shutdownThread.isAlive()) {
                    this.shutdownThread.interrupt();
                    this.shutdownThread.join(1_000);
                    if (this.shutdownThread.isAlive()) {
                        this.shutdownThread = null;
                        throw new RuntimeException("Failed to shutdown web server gracefully within the specified time.");
                    }
                }
                this.shutdownThread = null;
            }
        }
    }

    /**
     * Stop server.
     */
    private synchronized void stopServer() {
        if (this.server == null) {
            // Server is already stopped.
            return;
        }

        final Undertow serverToClose = this.server;
        this.server = null;
        final AtomicBoolean shutdownSuccessful = new AtomicBoolean();

        synchronized (this.shutdownLock) {
            this.shutdownThread = new Thread(() -> {
                if (shutdownSuccessful.get()) {
                    serverToClose.stop();
                    this.shutdownHooks.forEach(runnable -> {
                        try {
                            runnable.run();
                        } catch (final Exception e) {
                            UnderflowServerImpl.logger.error("An error occurred while running a shutdown hook", e);
                        }
                    });
                } else {
                    UnderflowServerImpl.logger.error("Failed to shutdown web server !");
                }
            });

            this.shutdownHandler.shutdown();
            this.shutdownHandler.addShutdownListener(success -> {
                shutdownSuccessful.set(success);
                this.shutdownThread.start();
                try {
                    Thread.sleep(100);
                } catch (final InterruptedException ignore) {
                }
                try {
                    this.triggerShutdownThread.lock();
                    this.condition.signalAll();
                } finally {
                    this.triggerShutdownThread.unlock();
                }
            });
        }
    }
}
