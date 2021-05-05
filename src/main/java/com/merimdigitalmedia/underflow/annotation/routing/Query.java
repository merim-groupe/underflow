package com.merimdigitalmedia.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    String value();

    boolean required() default false;
}
