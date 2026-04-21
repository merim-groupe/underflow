package com.merim.digitalpayment.underflow.openapi.filters;

import org.eclipse.microprofile.openapi.OASFilter;

/**
 * RegistrableOASFilter.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 24.06.11
 */
interface RegistrableOASFilter<T> extends OASFilter {

    /**
     * Dynamically register the given type on this filter.
     * This will be called right before calling any of the OASFilter methods.
     *
     * @param instance the instance
     */
    default void register(final T instance) {
    }
}
