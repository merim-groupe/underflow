package com.merim.digitalpayment.underflow.results.http;

import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * InputStreamHttpResult.
 *
 * @author Pierre Adam
 * @since 22.07.18
 */
public class InputStreamHttpResult extends BaseHttpResult {

    /**
     * The Data.
     */
    private final InputStream data;

    /**
     * The Io callback.
     */
    private final IoCallback ioCallback;

    /**
     * The Buffer.
     */
    private final byte[] buffer;

    /**
     * The Server exchange.
     */
    private HttpServerExchange serverExchange;

    /**
     * Instantiates a new Input stream http result.
     *
     * @param httpCode   the http code
     * @param data       the data
     * @param chunkSize  the chunk size
     * @param ioCallback the io callback
     */
    public InputStreamHttpResult(final int httpCode, final InputStream data, final int chunkSize, final IoCallback ioCallback) {
        super(httpCode);
        this.data = data;
        this.ioCallback = ioCallback;
        this.buffer = new byte[chunkSize];
    }

    /**
     * Instantiates a new Input stream http result.
     *
     * @param httpCode   the http code
     * @param data       the data
     * @param ioCallback the io callback
     */
    public InputStreamHttpResult(final int httpCode, final InputStream data, final IoCallback ioCallback) {
        this(httpCode, data, 1024 * 32, ioCallback);
    }

    /**
     * Instantiates a new Input stream http result.
     *
     * @param httpCode  the http code
     * @param data      the data
     * @param chunkSize the chunk size
     */
    public InputStreamHttpResult(final int httpCode, final InputStream data, final int chunkSize) {
        this(httpCode, data, chunkSize, IoCallback.END_EXCHANGE);
    }

    /**
     * Instantiates a new Input stream http result.
     *
     * @param httpCode the http code
     * @param data     the data
     */
    public InputStreamHttpResult(final int httpCode, final InputStream data) {
        this(httpCode, data, IoCallback.END_EXCHANGE);
    }

    @Override
    protected Consumer<HttpServerExchange> getLogic() {
        return exchange -> {
            this.serverExchange = exchange;
            this.accept(exchange.getResponseSender());
        };
    }

    /**
     * Accept.
     *
     * @param sender the sender
     */
    public void accept(final Sender sender) {
        try {
            final int length = this.data.read(this.buffer);
            if (length > -1) {
                final ByteBuffer wrap = ByteBuffer.wrap(this.buffer, 0, length);
                sender.send(wrap, new IoCallback() {
                    @Override
                    public void onComplete(final HttpServerExchange httpServerExchange, final Sender sender) {
                        InputStreamHttpResult.this.accept(sender);
                    }

                    @Override
                    public void onException(final HttpServerExchange httpServerExchange, final Sender sender, final IOException e) {
                        try {
                            InputStreamHttpResult.this.data.close();
                        } catch (final IOException ignore) {
                        }
                        InputStreamHttpResult.this.ioCallback.onException(httpServerExchange, sender, e);
                    }
                });
            } else {
                try {
                    this.data.close();
                } catch (final IOException ignore) {
                }
                this.ioCallback.onComplete(this.serverExchange, sender);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
