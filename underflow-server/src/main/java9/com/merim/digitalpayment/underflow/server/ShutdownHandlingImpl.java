package com.merim.digitalpayment.underflow.server;

import sun.misc.Signal;

/**
 * ShutdownHandlingImpl.
 *
 * @author Pierre Adam
 * @since 22.09.28
 */
@SuppressWarnings("restriction")
public class ShutdownHandlingImpl implements ShutdownHandling {

    @Override
    public void accept(final UnderflowServer underflowServer) {
        Signal.handle(new Signal("TERM"), sig -> underflowServer.stop()); // Handle SIGTERM.
        Signal.handle(new Signal("INT"), sig -> underflowServer.stop()); // Handle SIGINT.
        Runtime.getRuntime().addShutdownHook(new Thread(underflowServer::stop));
    }
}
