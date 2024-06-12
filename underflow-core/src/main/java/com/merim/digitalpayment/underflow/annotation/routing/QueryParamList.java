package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.*;

/**
 * QueryListProperty.
 *
 * @author Pierre Adam
 * @since 21.04.28
 */
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface QueryParamList {

    /**
     * Backed type class.
     *
     * @return the class
     */
    Class<?> value();
}
