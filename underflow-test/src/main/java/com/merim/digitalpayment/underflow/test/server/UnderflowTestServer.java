package com.merim.digitalpayment.underflow.test.server;

import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;

/**
 * UnderflowTestServer.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
public interface UnderflowTestServer {

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
     * Build test server underflow server.
     *
     * @param port the port
     * @return the underflow server
     */
    default UnderflowServer buildTestServer(final int port) {
        final UnderflowServer server = this.getUnderflowServerBuilder()
                .setPort(port)
                .setHost("0.0.0.0")
                .build();

        this.onServerCreated(server);
        return server;
    }
}
