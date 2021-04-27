package com.merimdigitalmedia.underflow.annotation.method;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DELETE.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DELETE {
}
