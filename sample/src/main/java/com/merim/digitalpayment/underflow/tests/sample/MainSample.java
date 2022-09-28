package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.handlers.flows.FlowAssetsHandler;
import com.merim.digitalpayment.underflow.handlers.flows.assets.ResourceAssetLoader;
import com.merim.digitalpayment.underflow.handlers.http.CORSHandler;
import com.merim.digitalpayment.underflow.handlers.http.CORSLegacyHandler;
import com.merim.digitalpayment.underflow.handlers.http.RequestLoggerHandler;
import com.merim.digitalpayment.underflow.server.UnderflowServer;

/**
 * MainTest.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class MainSample {

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        final UnderflowServer underflowServer = UnderflowServer.create()
                .addHttpListener(8080, "localhost")
                .withShutdownSignalHandling()
                .addPrefixPath("/", new RequestLoggerHandler(new HomeHandler()))
                .addPrefixPath("/assets", new RequestLoggerHandler(new FlowAssetsHandler(new ResourceAssetLoader(MainSample.class, "/assets"))))
                .addPrefixPath("/routes", new RequestLoggerHandler(new RouteTestHandler()))
                .addPrefixPath("/event", new RequestLoggerHandler(new ServerEventTestHandler()))
                .addPrefixPath("/api", new RequestLoggerHandler(new ApiTestHandler()))
                .addPrefixPath("/api/CORS", new RequestLoggerHandler(new CORSHandler(new ApiTestHandler())))
                .addPrefixPath("/api/CORSLegacy", new RequestLoggerHandler(new CORSLegacyHandler(new ApiTestHandler(), true)))
                .addPrefixPath("/prefix", new RequestLoggerHandler(new PathPrefixHandler()))
                .addToShutdown(() -> {
                    System.out.println("Shutting down server !");
                });

        System.out.println("Starting !");
        try {
            underflowServer.startAndWait();
        } catch (final InterruptedException e) {
            System.out.println("Something bad happened !");
            e.printStackTrace();
        }
    }
}
