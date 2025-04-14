package com.merim.digitalpayment.underflow.tests.sample.tools;

import java.io.IOException;
import java.io.InputStream;

/**
 * SimulatedSlowByteByByteInputStream.
 *
 * @author Pierre Adam
 * @since 25.04.14
 */
public class SimulatedSlowByteByByteInputStream extends InputStream {

    /**
     * The Wrapped stream.
     */
    private final InputStream wrappedStream;

    /**
     * Constructs a simulated slow input stream.
     *
     * @param wrappedStream The actual input stream to read from.
     */
    public SimulatedSlowByteByByteInputStream(final InputStream wrappedStream) {
        this.wrappedStream = wrappedStream;
    }

    @Override
    public int read() throws IOException {
        return this.wrappedStream.read();
    }

    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.wrappedStream.read(b, off, len > 0 ? 1 : 0);
    }

    @Override
    public void close() throws IOException {
        this.wrappedStream.close();
    }
}

