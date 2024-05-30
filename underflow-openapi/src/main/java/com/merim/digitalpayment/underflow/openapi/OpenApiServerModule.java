package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.server.*;
import com.merim.digitalpayment.underflow.server.modules.UnderflowServerModule;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.models.OpenAPI;

import java.util.Map;

/**
 * OpenApiServerModule.
 *
 * @author Pierre Adam
 * @since 24.05.27
 */
@Slf4j
public class OpenApiServerModule implements UnderflowServerModule {


    /**
     * The Open api.
     */
    private final OpenAPI openAPI;

    /**
     * Instantiates a new Open api server module.
     */
    public OpenApiServerModule() {
        this.openAPI = null;
    }

    @Override
    public int priority() {
        return 4000;
    }

    @Override
    public void register(final UnderflowServerBuilder builder) {
//        builder.addHandler("/openapi", new OpenApiHandler());
    }

    @Override
    public void onServerCreated(final UnderflowServer server) {
        if (server instanceof UnderflowServerImpl) {
            final UnderflowServerImpl serverImpl = (UnderflowServerImpl) server;
            final UnderflowApplication application = serverImpl.getApplication();
            final Map<String, HandlerData> handlers = serverImpl.getHandlers();
//            final OpenApiBuilder openApiBuilder = new OpenApiBuilder(application, handlers);

//            try {
//                this.openAPI = openApiBuilder.build();
//            } catch (final Exception e) {
//                OpenApiServerModule.logger.error("An error occurred while generating the OpenAPI file.", e);
//            }
        }
    }


}
