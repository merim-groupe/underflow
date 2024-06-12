package com.merim.digitalpayment.underflow.security.annotations;

import java.lang.annotation.*;

/**
 * Secured.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Secured {
}
