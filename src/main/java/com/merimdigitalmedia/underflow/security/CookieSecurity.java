package com.merimdigitalmedia.underflow.security;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

import java.lang.annotation.Annotation;
import java.sql.Date;
import java.time.Instant;
import java.util.Optional;

/**
 * CookieSecurity.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author Pierre Adam
 * @since 22.07.19
 */
public abstract class CookieSecurity<T, U extends Annotation> extends AFlowSecurity<T, U> {

    /**
     * The Cookie name.
     */
    private final String cookieName;

    /**
     * Instantiates a new Cookie security.
     *
     * @param cookieName              the cookie name
     * @param userRepresentationClass the user representation class
     * @param scopeClass              the scope class
     */
    public CookieSecurity(final String cookieName, final Class<T> userRepresentationClass, final Class<U> scopeClass) {
        super(userRepresentationClass, scopeClass);
        this.cookieName = cookieName;
    }

    @Override
    public Optional<T> isLogged(final HttpServerExchange exchange) {
        final Cookie sessionCookie = exchange.getRequestCookie(this.cookieName);

        if (sessionCookie == null) {
            return Optional.empty();
        }

        try {
            final Optional<T> optionalUser = Optional.ofNullable(this.deserialize(sessionCookie));
            optionalUser.flatMap(userRepresentation -> this.updateCookie(userRepresentation, sessionCookie)).ifPresent(exchange::setResponseCookie);
            return optionalUser;
        } catch (final Exception e) {
            // In case of error, clean the cookie.
            sessionCookie.setExpires(Date.from(Instant.EPOCH));
            exchange.setResponseCookie(sessionCookie);
        }

        return Optional.empty();
    }

    /**
     * Called after deserialize if deserialize returned a user representation.
     *
     * @param userRepresentation the user representation
     * @param sessionCookie      the session cookie
     * @return the boolean
     */
    protected Optional<Cookie> updateCookie(final T userRepresentation, final Cookie sessionCookie) {
        return Optional.empty();
    }

    /**
     * Serialize string.
     *
     * @param userRepresentation the user representation
     * @param baseCookie         the base cookie
     * @return the string
     */
    protected abstract Cookie serialize(T userRepresentation, Cookie baseCookie);

    /**
     * Deserialize optional.
     *
     * @param cookie the cookie
     * @return the optional
     * @throws Exception the exception
     */
    protected abstract T deserialize(Cookie cookie) throws Exception;

    /**
     * Sets cookie.
     *
     * @param userRepresentation the user representation
     * @return the cookie
     */
    public Cookie newCookie(final T userRepresentation) {
        return this.serialize(userRepresentation, new CookieImpl(this.cookieName));
    }
}
