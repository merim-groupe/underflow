package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.*;

/**
 * QueryParamRequired.
 *
 * @author Pierre Adam
 * @since 24.06.04
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface QueryParamRequired {
}
