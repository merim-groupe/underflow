package com.merim.digitalpayment.underflow.server;

import com.merim.digitalpayment.underflow.server.options.UnderflowOption;
import io.undertow.server.HttpHandler;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UnderflowServerBuilder.
 *
 * @author Pierre Adam
 * @since 24.04.23
 */
@Slf4j
public class UnderflowServerBuilder {

    /**
     * The Underlying handlers.
     */
    private final Map<String, HandlerData> handlers;

    /**
     * The Shutdown hooks.
     */
    private final List<Runnable> shutdownHooks;

    /**
     * The Port.
     */
    @Getter
    @Setter
    private int port;

    /**
     * The Host.
     */
    @Getter
    @Setter
    private String host;

    /**
     * Has the build been aborted.
     */
    private boolean aborted;

    /**
     * Instantiates a new Underflow server builder.
     *
     * @param host the host
     * @param port the port
     */
    UnderflowServerBuilder(@NonNull final String host, final int port) {
        this.handlers = new HashMap<>();
        this.shutdownHooks = new ArrayList<>();
        this.port = port;
        this.host = host;
        this.aborted = false;
    }

    /**
     * Add handler underflow server builder.
     *
     * @param prefix  the prefix
     * @param handler the handler
     * @param options the options
     * @return the underflow server builder
     */
    public UnderflowServerBuilder addHandler(@NonNull final String prefix,
                                             @NonNull final HttpHandler handler,
                                             final UnderflowOption... options) {
        this.handlers.put(prefix, new HandlerData(handler, options));
        return this;
    }

    /**
     * Add shutdown hook underflow server builder.
     *
     * @param hook the hook
     * @return the underflow server builder
     */
    public UnderflowServerBuilder addShutdownHook(@NonNull final Runnable hook) {
        this.shutdownHooks.add(hook);
        return this;
    }

    /**
     * Close at shutdown underflow server builder.
     *
     * @param closeable the closeable
     * @return the underflow server builder
     */
    public UnderflowServerBuilder closeAtShutdown(@NonNull final AutoCloseable closeable) {
        return this.addShutdownHook(() -> {
            try {
                closeable.close();
            } catch (final Exception e) {
                UnderflowServerBuilder.logger.error("An error occurred while shutting down.", e);
            }
        });
    }

    /**
     * Build underflow server.
     *
     * @return the underflow server
     */
    public UnderflowServer build() {
        if (this.aborted) {
            throw new IllegalStateException("Underflow server build has been previously aborted");
        }
        return new UnderflowServerImpl(this.host, this.port, this.handlers, this.shutdownHooks);
    }

    /**
     * Abort build and run shutdown hooks.
     */
    public void abortBuildAndShutdown() {
        this.aborted = true;
        this.shutdownHooks.forEach(Runnable::run);
    }
}
