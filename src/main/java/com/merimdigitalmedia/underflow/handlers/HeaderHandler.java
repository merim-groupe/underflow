package com.merimdigitalmedia.underflow.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.util.HashMap;
import java.util.Map;

/**
 * HeaderHandler.
 *
 * @author Pierre Adam
 * @since 21.07.22
 */
public class HeaderHandler extends PassthroughsHandler {

    /**
     * The Headers.
     */
    private final Map<HttpString, String> headers;

    /**
     * Instantiates a new Passthrough handler.
     *
     * @param underlying the underlying
     * @param headers    the headers
     */
    public HeaderHandler(final HttpHandler underlying, final Map<String, String> headers) {
        super(underlying);
        this.headers = this.asHttpStringMap(headers);
    }

    @Override
    protected void interceptRequest(final HttpServerExchange exchange) {
        this.headers.forEach((key, value) -> exchange.getResponseHeaders().put(key, value));
    }

    /**
     * As http string map map.
     *
     * @param map the map
     * @return the map
     */
    private Map<HttpString, String> asHttpStringMap(final Map<String, String> map) {
        final Map<HttpString, String> result = new HashMap<>();

        map.forEach((key, value) -> {
            result.put(new HttpString(key), value);
        });

        return result;
    }
}
