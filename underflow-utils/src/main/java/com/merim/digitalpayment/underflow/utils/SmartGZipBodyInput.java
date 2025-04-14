package com.merim.digitalpayment.underflow.utils;

import com.merim.digitalpayment.underflow.mdc.MDCContext;
import com.merim.digitalpayment.underflow.mdc.MDCKeys;
import io.undertow.server.HttpServerExchange;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

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
@Slf4j
public class SmartGZipBodyInput implements MDCContext {

    /**
     * The Exchange.
     */
    private final byte[] buffer;

    /**
     * Instantiates a new Body input.
     *
     * @param exchange the exchange
     */
    public SmartGZipBodyInput(@NonNull final HttpServerExchange exchange) {
        this(exchange.getInputStream());
    }

    /**
     * Instantiates a new Smart g zip body input.
     *
     * @param inputStream the input stream
     */
    public SmartGZipBodyInput(@NonNull final InputStream inputStream) {
        byte[] bytes = null;
        try {
            bytes = IOUtils.toByteArray(this.resolveStream(inputStream));
        } catch (final IOException e) {
            SmartGZipBodyInput.logger.error("An error occurred with the input stream.", e);
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
    private InputStream resolveStream(@NonNull final InputStream originalStream) throws IOException {
        final int standardGZipHeaderSize = 10;
        final PushbackInputStream inputStream = new PushbackInputStream(originalStream, 10);
        final byte[] headerBuffer = new byte[10];
        int bytesRead = 0, totalRead = 0;

        while (totalRead < standardGZipHeaderSize &&
                (bytesRead = inputStream.read(headerBuffer, totalRead, standardGZipHeaderSize - totalRead)) != -1) {
            totalRead += bytesRead;
        }

        inputStream.unread(headerBuffer, 0, totalRead);

        // If the size of the available bytes is bellow 10 (size of the GZip Header), return the stream as is.
        if (totalRead < standardGZipHeaderSize) {
            return inputStream;
        }

        if (headerBuffer[0] == (byte) 0x1f && headerBuffer[1] == (byte) 0x8b) {
            // GZip format detected !
            return new GZIPInputStream(inputStream);
        } else {
            // Non GZip format.
            return inputStream;
        }
    }
}
