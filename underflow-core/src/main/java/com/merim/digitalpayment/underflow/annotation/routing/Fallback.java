package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fallback.
 *
 * @author Lucas Stadelmann
 * @since 21.07.27
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Fallback {
}
