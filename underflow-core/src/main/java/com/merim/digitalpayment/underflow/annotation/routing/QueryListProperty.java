package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * QueryListProperty.
 *
 * @author Pierre Adam
 * @since 21.04.28
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryListProperty {

    /**
     * The interface No backed type.
     */
    interface NoBackedType {
    }

    /**
     * Backed type class.
     *
     * @return the class
     */
    Class<?> backedType() default NoBackedType.class;
}
