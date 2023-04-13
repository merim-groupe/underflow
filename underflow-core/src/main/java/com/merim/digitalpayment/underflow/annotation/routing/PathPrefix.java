package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * PathPrefix.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathPrefix {

    /**
     * Value string.
     *
     * @return the string
     */
    String value();
}
