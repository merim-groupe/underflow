/*
 * Copyright (C) 2014 - 2021 Merim Digitital Payment, SAS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.merimdigitalmedia.underflow.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.HashMap;
import java.util.Locale;

/**
 * CORSHandler.
 *
 * @author Pierre Adam
 * @since 21.07.21
 */
public class CORSHandler extends HeaderHandler {

    /**
     * Instantiates a new Cors handler.
     *
     * @param underlying               the underlying
     * @param accessControlAllowOrigin the access control allow origin
     */
    public CORSHandler(final HttpHandler underlying, final String accessControlAllowOrigin, final String accessControlAllowMethods) {
        super(underlying, new HashMap<String, String>() {{
            this.put("Access-Control-Allow-Origin", accessControlAllowOrigin);
            this.put("Access-Control-Allow-Methods", accessControlAllowMethods);
        }});
    }

    /**
     * Instantiates a new Cors handler.
     *
     * @param underlying               the underlying
     * @param accessControlAllowOrigin the access control allow origin
     */
    public CORSHandler(final HttpHandler underlying, final String accessControlAllowOrigin) {
        this(underlying, accessControlAllowOrigin, "GET,POST,PUT,PATCH,DELETE,OPTIONS,HEAD");
    }

    /**
     * Instantiates a new Cors handler.
     *
     * @param underlying the underlying
     */
    public CORSHandler(final HttpHandler underlying) {
        this(underlying, "*");
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        if (exchange.getRequestMethod().toString().toUpperCase(Locale.ROOT).equals("OPTIONS")) {
            exchange.setStatusCode(200);
            exchange.endExchange();
        } else {
            super.handleRequest(exchange);
        }
    }
}
