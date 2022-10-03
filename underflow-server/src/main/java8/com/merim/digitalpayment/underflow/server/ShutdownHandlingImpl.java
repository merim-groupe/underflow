package com.merim.digitalpayment.underflow.server;

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
        Runtime.getRuntime().addShutdownHook(new Thread(underflowServer::stop));
    }
}
