package com.merim.digitalpayment.underflow.security;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;

import java.lang.annotation.Annotation;
import java.util.Optional;

/**
 * HeaderSecurity.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author Pierre Adam
 * @since 22.07.19
 */
public abstract class HeaderSecurity<T, U extends Annotation> extends AFlowSecurity<T, U> {

    /**
     * Instantiates a new Header security.
     *
     * @param userRepresentationClass the user representation class
     * @param scopeClass              the scope class
     */
    public HeaderSecurity(final Class<T> userRepresentationClass, final Class<U> scopeClass) {
        super(userRepresentationClass, scopeClass);
    }

    @Override
    public Optional<T> isLogged(final HttpServerExchange exchange) {
        final HeaderValues authorization = exchange.getRequestHeaders().get(new HttpString("authorization"));

        if (authorization == null || authorization.get(0) == null || authorization.get(0).trim().isEmpty()) {
            return Optional.empty();
        }

        String value = authorization.get(0).trim();
        if (value.toLowerCase().startsWith("bearer ")) {
            value = value.substring(7).trim();
        }

        return this.isLogged(value);
    }

    /**
     * Is logged optional.
     *
     * @param value the value
     * @return the optional
     */
    protected abstract Optional<T> isLogged(final String value);
}
