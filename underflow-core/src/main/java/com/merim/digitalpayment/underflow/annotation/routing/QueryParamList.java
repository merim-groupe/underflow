package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * QueryListProperty.
 *
 * @author Pierre Adam
 * @since 21.04.28
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParamList {

    /**
     * Backed type class.
     *
     * @return the class
     */
    Class<?> value();
}
