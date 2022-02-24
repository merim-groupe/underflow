package com.merimdigitalmedia.underflow.tests;

import com.merimdigitalmedia.underflow.handlers.CORSHandler;
import com.merimdigitalmedia.underflow.handlers.CORSLegacyAllowHandler;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

/**
 * MainTest.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class MainTest {

    public static void main(final String[] args) {
        final PathHandler handler = new PathHandler();

        handler.addPrefixPath("/", new TestHandler());
        handler.addPrefixPath("/api", new ApiTestHandler());
        handler.addPrefixPath("/CORS/Legacy", new CORSLegacyAllowHandler(new TestHandler(), true));
        handler.addPrefixPath("/CORS", new CORSHandler(new TestHandler()));
        handler.addPrefixPath("/event", new ServerEventTestHandler());

        final Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build();

        System.out.println("Starting !");
        server.start();
    }
}
