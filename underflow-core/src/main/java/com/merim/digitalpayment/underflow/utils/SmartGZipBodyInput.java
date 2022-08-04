package com.merim.digitalpayment.underflow.utils;

import com.merim.digitalpayment.underflow.mdc.MDCContext;
import com.merim.digitalpayment.underflow.mdc.MDCKeys;
import io.undertow.server.HttpServerExchange;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
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
public class SmartGZipBodyInput implements MDCContext {

    /**
     * The Logger.
     */
    private final Logger logger;

    /**
     * The Exchange.
     */
    private final byte[] buffer;

    /**
     * Instantiates a new Body input.
     *
     * @param exchange the exchange
     */
    public SmartGZipBodyInput(final HttpServerExchange exchange) {
        this(exchange.getInputStream());
    }

    /**
     * Instantiates a new Smart g zip body input.
     *
     * @param inputStream the input stream
     */
    public SmartGZipBodyInput(final InputStream inputStream) {
        this.logger = LoggerFactory.getLogger(SmartGZipBodyInput.class);

        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(this.resolveStream(inputStream));
        } catch (final IOException e) {
            this.logger.error("An error occurred with the input stream.", e);
        }
        this.buffer = bytes != null ? bytes : new byte[]{};

        this.putMDC(MDCKeys.Request.BODY, new String(this.buffer));
    }

    /**
     * Gets input stream.
     *
     * @return the input stream
     */
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.buffer);
    }

    /**
     * Gets input stream.
     *
     * @return the input stream
     */
    public byte[] getBytes() {
        return this.buffer;
    }

    /**
     * Resolve stream input stream.
     *
     * @param originalStream the original stream
     * @return the input stream
     * @throws IOException the io exception
     */
    private InputStream resolveStream(final InputStream originalStream) throws IOException {
        final PushbackInputStream inputStream = new PushbackInputStream(originalStream, 2);

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
