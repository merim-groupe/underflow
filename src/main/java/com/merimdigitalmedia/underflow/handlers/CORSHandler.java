/*
 * Copyright (C) 2014 - 2021 Merim Digitital Payment, SAS - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.merimdigitalmedia.bkclerkmaster.handlers.http;

import com.merimdigitalmedia.underflow.handlers.HeaderHandler;
import io.undertow.server.HttpHandler;

import java.util.HashMap;

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
    public CORSHandler(final HttpHandler underlying, final String accessControlAllowOrigin) {
        super(underlying, new HashMap<String, String>() {{
            this.put("Access-Control-Allow-Origin", accessControlAllowOrigin);
        }});
    }

    /**
     * Instantiates a new Cors handler.
     *
     * @param underlying the underlying
     */
    public CORSHandler(final HttpHandler underlying) {
        this(underlying, "*");
    }
}
