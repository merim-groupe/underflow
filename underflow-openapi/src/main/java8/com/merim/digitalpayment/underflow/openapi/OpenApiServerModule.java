package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import com.merim.digitalpayment.underflow.server.modules.UnderflowServerModule;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;

import java.util.Optional;

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

    /**
     * Gets open api.
     *
     * @return the open api
     */
    public Optional<OpenAPI> getOpenAPI() {
        return Optional.empty();
    }

    /**
     * Priority int.
     *
     * @return the int
     */
    @Override
    public int priority() {
        return 1000;
    }

    /**
     * Register.
     *
     * @param builder the builder
     */
    @Override
    public void register(final UnderflowServerBuilder builder) {
    }

    /**
     * On server created.
     *
     * @param server the server
     */
    @Override
    public void onServerCreated(final UnderflowServer server) {
    }
}
