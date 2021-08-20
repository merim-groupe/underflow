package com.merimdigitalmedia.underflow.utils;

import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.zip.GZIPInputStream;

/**
 * SmartGZipBodyInput.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
public class SmartGZipBodyInput {

    /**
     * The Exchange.
     */
    private final HttpServerExchange exchange;

    /**
     * Instantiates a new Body input.
     *
     * @param exchange the exchange
     */
    public SmartGZipBodyInput(final HttpServerExchange exchange) {
        this.exchange = exchange;
    }

    /**
     * Gets input stream.
     *
     * @return the input stream
     * @throws IOException the io exception
     */
    public InputStream getInputStream() throws IOException {
        final PushbackInputStream inputStream = new PushbackInputStream(this.exchange.getInputStream(), 2);

        // If the size of the available bytes is bellow 10 (size of the GZip Header), return the stream as is.
        if (inputStream.available() < 10) {
            return inputStream;
        }

        final byte[] buff = new byte[2];
        final int read = inputStream.read(buff);
        assert read == 2;
        inputStream.unread(buff);

        if (buff[0] == (byte) 0x1f && buff[1] == (byte) 0x8b) {
            // GZip format detected !
            return new GZIPInputStream(inputStream);
        } else {
            // Non GZip format.
            return inputStream;
        }
    }
}
