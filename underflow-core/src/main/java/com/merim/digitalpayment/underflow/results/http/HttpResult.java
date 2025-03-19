package com.merim.digitalpayment.underflow.results.http;

import com.merim.digitalpayment.underflow.results.Result;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

import java.util.Collection;
import java.util.Map;

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
     * With header http result.
     *
     * @param headers the headers
     * @return the http result
     */
    default HttpResult withHeaders(final Map<HttpString, String> headers) {
        headers.forEach(this::withHeader);
        return this;
    }

    /**
     * With content type result.
     *
     * @param contentType the content type
     * @return the result
     */
    default HttpResult withContentType(final String contentType) {
        return this.withHeader(Headers.CONTENT_TYPE, contentType);
    }

    /**
     * With cookie result.
     *
     * @param cookie the cookie
     * @return the result
     */
    HttpResult withCookie(Cookie cookie);

    /**
     * With cookies http result.
     *
     * @param cookies the cookies
     * @return the http result
     */
    default HttpResult withCookies(final Collection<Cookie> cookies) {
        cookies.forEach(this::withCookie);
        return this;
    }

    /**
     * Delete cookie http result.
     *
     * @param name the name
     * @return the http result
     */
    HttpResult deleteCookie(String name);

    /**
     * Delete cookies http result.
     *
     * @param cookieNames the cookie names
     * @return the http result
     */
    default HttpResult deleteCookies(final Collection<String> cookieNames) {
        cookieNames.forEach(this::deleteCookie);
        return this;
    }

    /**
     * Drop cookies http result.
     *
     * @return the http result
     */
    HttpResult dropCookies();
}
