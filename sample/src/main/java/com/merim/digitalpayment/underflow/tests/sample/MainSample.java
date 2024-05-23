package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.handlers.flows.FlowAssetsHandler;
import com.merim.digitalpayment.underflow.handlers.flows.assets.ResourceAssetLoader;
import com.merim.digitalpayment.underflow.handlers.http.CORSHandler;
import com.merim.digitalpayment.underflow.handlers.http.IDontCareAboutCORSPleaseHelpHandler;
import com.merim.digitalpayment.underflow.server.UnderflowApplication;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import com.merim.digitalpayment.underflow.server.options.UnderflowCORSOption;
import com.merim.digitalpayment.underflow.server.options.UnderflowLoggerOption;
import lombok.NoArgsConstructor;

/**
 * MainTest.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@NoArgsConstructor
public class MainSample implements UnderflowApplication {

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        UnderflowApplication.run(MainSample.class, args);
    }

    @Override
    public void initialize(final String[] args) {
    }

    @Override
    public UnderflowServerBuilder createServerBuilder() {
        return UnderflowServer.builder("localhost", 8080)
                .addHandler("/", new HomeHandler(), UnderflowCORSOption.enableEasyCORS(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler("/assets", new FlowAssetsHandler(new ResourceAssetLoader(MainSample.class, "/assets")), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler("/routes", new RouteTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler("/event", new ServerEventTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler("/api", new ApiTestHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler("/api/CORS", new CORSHandler(new ApiTestHandler()), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler("/api/CORSLegacy", new IDontCareAboutCORSPleaseHelpHandler(new ApiTestHandler(), true), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addHandler("/prefix", new PathPrefixHandler(), UnderflowLoggerOption.LOG_ALL_QUERY)
                .addShutdownHook(() -> System.out.println("Shutting down server !"));
    }

    @Override
    public void onServerCreated(final UnderflowServer server) {
        Application.register(UnderflowServer.class, server);
    }
}
