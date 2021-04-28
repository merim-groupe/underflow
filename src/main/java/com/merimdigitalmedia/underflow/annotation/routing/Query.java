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
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    String[] parameters() default {};
}
