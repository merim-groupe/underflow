package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MultipleRecallNamed.
 *
 * @author Pierre Adam
 * @since 23.04.19
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipleRecallNamed {

    /**
     * Array of recall.
     *
     * @return the array of recall
     */
    RecallNamed[] value();
}
