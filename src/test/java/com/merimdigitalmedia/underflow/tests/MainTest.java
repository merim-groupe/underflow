package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.handlers.http.CORSHandler;
import com.merimdigitalmedia.underflow.handlers.http.CORSLegacyAllowHandler;
import com.merimdigitalmedia.underflow.handlers.http.RequestLoggerHandler;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

/**
 * MainTest.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class MainTest {

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        final PathHandler handler = new PathHandler();

        handler.addPrefixPath("/", new RequestLoggerHandler(new HomeHandler()));
        handler.addPrefixPath("/api", new RequestLoggerHandler(new ApiTestHandler()));
        handler.addPrefixPath("/CORS/Legacy", new RequestLoggerHandler(new CORSLegacyAllowHandler(new HomeHandler(), true)));
        handler.addPrefixPath("/CORS", new RequestLoggerHandler(new CORSHandler(new HomeHandler())));
//        handler.addPrefixPath("/event", new RequestLoggerHandler(new ServerEventTestHandler()));

        final Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build();

        System.out.println("Starting !");
        server.start();
    }
}
