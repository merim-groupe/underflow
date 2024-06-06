package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * QueryParamRequired.
 *
 * @author Pierre Adam
 * @since 24.06.04
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParamRequired {
}
