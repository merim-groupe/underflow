package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.*;

/**
 * RecallNamed.
 *
 * @author Pierre Adam
 * @since 23.04.19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(MultipleRecallNamed.class)
public @interface RecallNamed {

    /**
     * Value string.
     *
     * @return the string
     */
    String value();

    /**
     * Fail on missing boolean.
     *
     * @return the boolean
     */
    boolean failOnMissing() default false;
}
