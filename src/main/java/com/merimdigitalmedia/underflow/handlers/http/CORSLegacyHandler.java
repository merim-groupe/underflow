/*
 * Copyright (C) 2014 - 2021 Merim Digitital Payment, SAS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.merimdigitalmedia.underflow.handlers.http;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;

import java.util.HashMap;
import java.util.Locale;

/**
 * CORSLegacyAllowHandler.
 * <p>
 * DO NOT USE THIS HANDLER... EXCEPT IF YOU KNOW WHAT YOU'RE DOING !
 * In case of doubt, use CORSHandler.
 *
 * @author Pierre Adam
 * @since 21.12.15
 */
public class CORSLegacyHandler extends HeaderHandler {

    /**
     * Instantiates a new Cors handler.
     *
     * @param underlying                    the underlying
     * @param accessControlAllowCredentials the access control allow credentials
     */
    public CORSLegacyHandler(final HttpHandler underlying,
                             final boolean accessControlAllowCredentials) {
        super(underlying, new HashMap<String, String>() {{
            this.put("Access-Control-Allow-Origin", "*");
            this.put("Access-Control-Allow-Methods", "*");
            this.put("Access-Control-Allow-Headers", "*");
            this.put("Access-Control-Allow-Credentials", "true");
            this.put("Access-Control-Max-Age", "3600");
        }});
    }

    @Override
    protected void callUnderlying(final HttpServerExchange exchange) throws Exception {
        if (exchange.getRequestMethod().toString().toUpperCase(Locale.ROOT).equals("OPTIONS")) {
            final HeaderMap requestHeaders = exchange.getRequestHeaders();
            final HeaderMap responseHeaders = exchange.getResponseHeaders();

            this.CORSHeader(exchange, "Access-Control-Request-Headers", "Access-Control-Allow-Headers");
            this.CORSHeader(exchange, "Access-Control-Request-Method", "Access-Control-Allow-Methods");

            exchange.setStatusCode(200);
            exchange.endExchange();
        } else {
            this.underlying.handleRequest(exchange);
        }
    }

    /**
     * Cors header.
     *
     * @param exchange           the exchange
     * @param requestCORSHeader  the request cors header
     * @param responseCORSHeader the response cors header
     */
    protected void CORSHeader(final HttpServerExchange exchange, final String requestCORSHeader, final String responseCORSHeader) {
        final HeaderMap requestHeaders = exchange.getRequestHeaders();
        final HeaderMap responseHeaders = exchange.getResponseHeaders();

        if (requestHeaders.contains(requestCORSHeader)) {
            responseHeaders.put(new HttpString(responseCORSHeader), requestHeaders.get(requestCORSHeader).getFirst());
        } else {
            responseHeaders.put(new HttpString(responseCORSHeader), "*");
        }
    }
}
