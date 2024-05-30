package com.merim.digitalpayment.underflow.server;

import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
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
 * UnderflowServerBuilderImpl.
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
     * The Class loader.
     */
    @Getter
    @Setter
    private ClassLoader classLoader;

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
        this.classLoader = null;
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
     * @deprecated use either addHandler without the prefix or addUncontrolledHandler
     */
    @Deprecated
    public UnderflowServerBuilder addHandler(@NonNull final String prefix,
                                             @NonNull final HttpHandler handler,
                                             final UnderflowOption... options) {
        return this.internalAddHandler(prefix, new HandlerData(handler, options));
    }

    /**
     * Add handler underflow server builder.
     *
     * @param flowHandler the flow handler
     * @param options     the options
     * @return the underflow server builder
     */
    public UnderflowServerBuilder addHandler(@NonNull final FlowHandler flowHandler,
                                             final UnderflowOption... options) {
        return this.internalAddHandler(flowHandler.getHandlerInfo().getNonVariablePath(), new HandlerData(flowHandler, options));
    }

    /**
     * Add uncontrolled handler underflow server builder.
     * It is not recommended to use this type of handler since this is only for compatibility reason with low level undertow handler.
     * If for any reason you need to be able to add a low level handler before the actual handler,
     * it is a better option to use the options to do so.
     *
     * @param prefix  the prefix
     * @param handler the handler
     * @param options the options
     * @return the underflow server builder
     */
    public UnderflowServerBuilder addUncontrolledHandler(@NonNull final String prefix,
                                                         @NonNull final HttpHandler handler,
                                                         final UnderflowOption... options) {
        return this.internalAddHandler(prefix, new HandlerData(handler, options));
    }

    /**
     * Internal add handler underflow server builder.
     *
     * @param prefix      the prefix
     * @param handlerData the handler data
     * @return the underflow server builder
     */
    private UnderflowServerBuilder internalAddHandler(@NonNull final String prefix,
                                                      @NonNull final HandlerData handlerData) {
        if (this.handlers.containsKey(prefix)) {
            throw new RuntimeException("The prefix " + prefix + " is already registered." +
                    "If you are using @Path with a variable on your handler, " +
                    "only the part before the variable is considered for the main routing.");
        }

        this.handlers.put(prefix, handlerData);

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
     * @param application the application
     * @return the underflow server
     */
    public UnderflowServer build(final UnderflowApplication application) {
        if (this.aborted) {
            throw new IllegalStateException("Underflow server build has been previously aborted");
        }
        return new UnderflowServerImpl(application, this.classLoader, this.host, this.port, this.handlers, this.shutdownHooks);
    }

    /**
     * Abort build and run shutdown hooks.
     */
    public void abortBuildAndShutdown() {
        this.aborted = true;
        this.shutdownHooks.forEach(Runnable::run);
    }
}
