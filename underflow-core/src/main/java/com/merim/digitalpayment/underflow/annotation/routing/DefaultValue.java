package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * DefaultValue.
 *
 * @author Lucas Stadelmann
 * @since 21.08.23
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultValue {

    /**
     * The default value.
     *
     * @return the default string
     */
    String[] value() default {};
}
