package com.merim.digitalpayment.underflow.server.options;

import io.undertow.server.HttpHandler;

/**
 * UnderflowOption.
 *
 * @author Pierre Adam
 * @since 24.04.23
 */
public interface UnderflowOption {

    /**
     * Alter path string.
     *
     * @param path    the path
     * @param handler the handler
     * @return the string
     */
    default String alterPath(final String path, final HttpHandler handler) {
        return path;
    }

    /**
     * Http handler http handler.
     *
     * @param path    the path
     * @param handler the handler
     * @return the http handler
     */
    default HttpHandler alterHandler(final String path, final HttpHandler handler) {
        return handler;
    }
}
