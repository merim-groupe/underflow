package com.merimdigitalmedia.underflow.tests;

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

        handler.addPrefixPath("/test", new TestHandler());
        handler.addPrefixPath("/event", new ServerEventTestHandler());

        final Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build();

        System.out.println("Starting !");
        server.start();
    }
}
