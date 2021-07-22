package com.merimdigitalmedia.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Parameter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {
    /**
     * Value string.
     *
     * @return the string
     */
    String value();
}
