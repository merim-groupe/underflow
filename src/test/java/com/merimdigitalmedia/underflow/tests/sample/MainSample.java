package com.merimdigitalmedia.underflow.tests.sample;

import com.merimdigitalmedia.underflow.handlers.flows.FlowAssetsHandler;
import com.merimdigitalmedia.underflow.handlers.flows.assets.ResourceAssetLoader;
import com.merimdigitalmedia.underflow.handlers.http.CORSHandler;
import com.merimdigitalmedia.underflow.handlers.http.CORSLegacyHandler;
import com.merimdigitalmedia.underflow.handlers.http.RequestLoggerHandler;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

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
        final PathHandler handler = new PathHandler();

        handler.addPrefixPath("/", new RequestLoggerHandler(new HomeHandler()));
        handler.addPrefixPath("/assets", new RequestLoggerHandler(new FlowAssetsHandler(new ResourceAssetLoader(MainSample.class, "/assets"))));
        handler.addPrefixPath("/routes", new RequestLoggerHandler(new RouteTestHandler()));
        handler.addPrefixPath("/event", new RequestLoggerHandler(new ServerEventTestHandler()));
        handler.addPrefixPath("/api", new RequestLoggerHandler(new ApiTestHandler()));
        handler.addPrefixPath("/api/CORS", new RequestLoggerHandler(new CORSHandler(new ApiTestHandler())));
        handler.addPrefixPath("/api/CORSLegacy", new RequestLoggerHandler(new CORSLegacyHandler(new ApiTestHandler(), true)));

        final Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build();

        System.out.println("Starting !");
        server.start();
    }
}
