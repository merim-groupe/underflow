package com.merimdigitalmedia.underflow.security;

import java.lang.annotation.Annotation;

/**
 * AFlowSecurity.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author Pierre Adam
 * @since 22.07.20
 */
public abstract class AFlowSecurity<T, U extends Annotation> implements FlowSecurity<T, U> {

    /**
     * The Scope class.
     */
    private final Class<U> scopeClass;


    /**
     * The User representation class.
     */
    private final Class<T> userRepresentationClass;

    /**
     * Instantiates a new A flow security.
     *
     * @param userRepresentationClass the user representation class
     * @param scopeClass              the scope class
     */
    public AFlowSecurity(final Class<T> userRepresentationClass, final Class<U> scopeClass) {
        this.userRepresentationClass = userRepresentationClass;
        this.scopeClass = scopeClass;
    }

    @Override
    public Class<U> scopeAnnotationClass() {
        return this.scopeClass;
    }

    @Override
    public Class<T> userRepresentationClass() {
        return this.userRepresentationClass;
    }
}
