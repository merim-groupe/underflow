package com.merimdigitalmedia.underflow.security;

import io.undertow.server.HttpServerExchange;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * FlowSecurity.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author Pierre Adam
 * @since 22.07.19
 */
public interface FlowSecurity<T, U extends Annotation> {

    /**
     * Is the user logged.
     *
     * @param exchange the exchange
     * @return the user representation or empty optional.
     */
    Optional<T> isLogged(HttpServerExchange exchange);

    /**
     * Is logged optional.
     *
     * @param userRepresentation the user representation
     * @param method             the method
     * @return the optional
     */
    @SuppressWarnings("unchecked")
    default boolean isAccessibleUnsafe(final Object userRepresentation, final Method method) {
        return this.isAccessible((T) userRepresentation, method);
    }

    /**
     * Is logged optional.
     *
     * @param userRepresentation the user representation
     * @param method             the method
     * @return the optional
     */
    default boolean isAccessible(final T userRepresentation, final Method method) {
        final U scope = method.getAnnotation(this.scopeAnnotationClass());

        return this.isAccessible(userRepresentation, scope);
    }

    /**
     * Is logged authorization result.
     *
     * @param userRepresentation the user representation
     * @param scope              the scope
     * @return the user representation or empty optional.
     */
    boolean isAccessible(T userRepresentation, U scope);

    /**
     * User representation class class.
     *
     * @return the class
     */
    Class<T> userRepresentationClass();

    /**
     * Scope annotation class.
     *
     * @return the class
     */
    Class<U> scopeAnnotationClass();
}
