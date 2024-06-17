package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.*;

/**
 * PathIgnoreCase.
 *
 * @author Pierre Adam
 * @since 24.06.17
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PathIgnoreCase {
}
