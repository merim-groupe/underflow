package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.server.modules.UnderflowServerModule;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.OASFilter;

/**
 * OpenApiServerModule.
 *
 * @author Pierre Adam
 * @since 24.05.27
 */
@Slf4j
public class OpenApiServerModule implements UnderflowServerModule {

    /**
     * Instantiates a new Open api server module.
     *
     * @param oasFilters the oas filters
     */
    public OpenApiServerModule(final OASFilter... oasFilters) {
        this(OpenApiUiFlavor.SWAGGER_UI, oasFilters);
    }

    /**
     * Instantiates a new Open api server module.
     *
     * @param uiFlavor   the ui flavor
     * @param oasFilters the oas filters
     */
    public OpenApiServerModule(final OpenApiUiFlavor uiFlavor,
                               final OASFilter... oasFilters) {
        OpenApiServerModule.logger.info("OpenAPI is disabled for Java version anterior to 11.");
    }
}
