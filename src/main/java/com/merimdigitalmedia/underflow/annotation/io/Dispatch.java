package com.merimdigitalmedia.underflow.annotation.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Dispatch.
 *
 * @author Pierre Adam
 * @since 22.07.18
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dispatch {

    /**
     * Value string.
     *
     * @return the string
     */
    boolean block() default true;
}
