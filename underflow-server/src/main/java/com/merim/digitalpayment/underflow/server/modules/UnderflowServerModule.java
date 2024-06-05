package com.merim.digitalpayment.underflow.server.modules;

import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;

/**
 * UnderflowServerModule.
 *
 * @author Pierre Adam
 * @since 24.05.27
 */
public interface UnderflowServerModule {

    /**
     * Priority int.
     * Lowest value will be initialized first.
     *
     * @return the int
     */
    default int priority() {
        return 0;
    }

    /**
     * The module can register itself at the builder level if necessary.
     *
     * @param builder the builder
     */
    default void register(final UnderflowServerBuilder builder) {
    }

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
     * On server stop.
     *
     * @return the runnable
     */
    default Runnable onServerStop() {
        return null;
    }
}
