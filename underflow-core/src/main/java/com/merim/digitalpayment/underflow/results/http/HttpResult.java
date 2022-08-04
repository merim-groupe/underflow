package com.merim.digitalpayment.underflow.results.http;

import com.merim.digitalpayment.underflow.results.Result;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HttpString;

/**
 * HttpResult.
 *
 * @author Pierre Adam
 * @since 22.07.18
 */
public interface HttpResult extends Result {

    /**
     * Add header to the result.
     *
     * @param key   the key
     * @param value the value
     * @return the result
     */
    HttpResult withHeader(final HttpString key, final String value);

    /**
     * Add header to the result.
     *
     * @param key   the key
     * @param value the value
     * @return the result
     */
    default HttpResult withHeader(final String key, final String value) {
        return this.withHeader(new HttpString(key), value);
    }

    /**
     * With cookie result.
     *
     * @param cookie the cookie
     * @return the result
     */
    HttpResult withCookie(Cookie cookie);

    /**
     * Drop cookies http result.
     *
     * @return the http result
     */
    HttpResult dropCookies();

    /**
     * With content type result.
     *
     * @param contentType the content type
     * @return the result
     */
    HttpResult withContentType(final String contentType);
}
