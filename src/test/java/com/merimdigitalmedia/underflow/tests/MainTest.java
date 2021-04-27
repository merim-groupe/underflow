package com.merimdigitalmedia.underflow.tests;

import io.undertow.Undertow;

/**
 * MainTest.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class MainTest {

    public static void main(final String[] args) {
        final Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(new MyHandler())
                .build();
        server.start();
    }
}
