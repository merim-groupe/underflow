package com.merim.digitalpayment.underflow.openapi.filters;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.app.Mode;
import com.merim.digitalpayment.underflow.server.UnderflowServerImpl;

/**
 * OpenAPI filter that automatically resolves and sets the OpenAPI specification version
 * from the main application package's implementation version.
 * <p>
 * This filter extends {@link OpenApiVersionFilter} and implements {@link ServerAwareOASFilter}
 * to automatically extract the version from the Underflow application's package metadata.
 * If the package implementation version is not available, it defaults to "Development" when
 * running in development mode, or "Unresolved" otherwise.
 * </p>
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
    public void register(final UnderflowServerImpl underflowServer) {
        final Package appPackage = underflowServer.getApplication().getClass().getPackage();

        if (appPackage != null && appPackage.getImplementationVersion() != null) {
            this.version = appPackage.getImplementationVersion();
        } else if (Application.getMode() == Mode.DEV) {
            this.version = "Development";
        } else {
            this.version = "Unresolved";
        }
    }
}
