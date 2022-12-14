package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Path.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@Repeatable(Paths.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    /**
     * Value string.
     *
     * @return the string
     */
    String value();
    
    /**
     * Ignore case boolean.
     *
     * @return the boolean
     */
    boolean ignoreCase() default false;
}
