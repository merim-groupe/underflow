package com.merim.digitalpayment.underflow.openapi.filters;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.app.Mode;
import com.merim.digitalpayment.underflow.server.UnderflowServerImpl;

/**
 * OpenApiAutoResolveVersionFilter.
 *
 * @author Pierre Adam
 * @since 24.06.11
 */
public class OpenApiAutoResolveVersionFilter extends OpenApiVersionFilter implements ServerAwareOASFilter {

    /**
     * Instantiates a new Open api auto resolve version filter.
     */
    public OpenApiAutoResolveVersionFilter() {
        super(null);
    }

    @Override
    public void registerServer(final UnderflowServerImpl underflowServer) {
        if (Application.getMode() != Mode.DEV) {
            final Package appPackage = underflowServer.getApplication().getClass().getPackage();

            if (appPackage != null && appPackage.getImplementationVersion() != null) {
                this.version = appPackage.getImplementationVersion();
            } else {
                this.version = "Unresolved";
            }
        }
    }
}
