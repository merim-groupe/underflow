package com.merim.digitalpayment.underflow.test.server;

import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;

/**
 * UnderflowTestApplication.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
public interface UnderflowTestApplication {

    /**
     * Gets underflow server.
     *
     * @return the underflow server
     */
    UnderflowServerBuilder getUnderflowServerBuilder();

    /**
     * On server created.
     *
     * @param server the server
     */
    void onServerCreated(final UnderflowServer server);

    /**
     * On server created.
     *
     * @param server the server
     */
    void onServerStart(final UnderflowServer server);

    /**
     * On server stop.
     *
     * @param server the server
     */
    default void onServerStop(final UnderflowServer server) {
    }
}
