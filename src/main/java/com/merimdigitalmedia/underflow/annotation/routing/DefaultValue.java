package com.merimdigitalmedia.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DefaultValue.
 *
 * @author Lucas Stadelmann
 * @since 21.08.23
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {

    /**
     * The default value.
     *
     * @return the default string
     */
    String value();
}
