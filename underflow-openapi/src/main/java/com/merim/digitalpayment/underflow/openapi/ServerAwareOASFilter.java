package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.server.UnderflowServerImpl;
import org.eclipse.microprofile.openapi.OASFilter;

/**
 * ServerAwareOASFilter.
 *
 * @author Pierre Adam
 * @since 24.06.11
 */
public interface ServerAwareOASFilter extends OASFilter {

    /**
     * Dynamically register the underflow server called for this filter.
     * This will be called right before call any of the OASFilter methods.
     *
     * @param underflowServer the underflow server
     */
    default void registerServer(final UnderflowServerImpl underflowServer) {
    }
}
