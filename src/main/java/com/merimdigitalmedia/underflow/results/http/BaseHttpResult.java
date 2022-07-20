package com.merimdigitalmedia.underflow.results.http;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

/**
 * BaseHttpResult.
 *
 * @author Pierre Adam
 * @since 22.07.18
 */
public abstract class BaseHttpResult implements HttpResult {

    /**
     * The Headers.
     */
    private final Map<HttpString, String> headers;

    /**
     * The Cookies.
     */
    private final List<Cookie> cookies;

    /**
     * The Clear cookies.
     */
    private boolean clearCookies;

    /**
     * The Http code.
     */
    private final int httpCode;

    /**
     * Instantiates a new Base result.
     *
     * @param httpCode the http code
     */
    protected BaseHttpResult(final int httpCode) {
        this.headers = new HashMap<>();
        this.cookies = new ArrayList<>();
        this.clearCookies = false;
        this.httpCode = httpCode;
    }

    /**
     * Gets logic.
     *
     * @return the logic
     */
    protected abstract Consumer<HttpServerExchange> getLogic();

    @Override
    public void process(final HttpServerExchange exchange) {
        final HeaderMap responseHeaders = exchange.getResponseHeaders();

        this.headers.forEach(responseHeaders::put);

        if (this.clearCookies) {
            for (final Cookie next : exchange.requestCookies()) {
                exchange.setResponseCookie(next.setExpires(Date.from(Instant.EPOCH)));
            }
        }

        this.cookies.forEach(exchange::setResponseCookie);

        exchange.setStatusCode(this.httpCode);
        this.getLogic().accept(exchange);

        // This is just a safety. Exchange should already be closed.
        exchange.endExchange();
    }

    @Override
    public HttpResult withHeader(final HttpString key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public HttpResult withCookie(final Cookie cookie) {
        this.cookies.add(cookie);
        return this;
    }

    @Override
    public HttpResult dropCookies() {
        this.clearCookies = true;
        return this;
    }

    @Override
    public HttpResult withContentType(final String contentType) {
        return this.withHeader("content-type", contentType);
    }

    @Override
    public HttpResult asJson() {
        return this.withContentType("application/json");
    }
}
