package com.merim.digitalpayment.underflow.openapi.annotations;

import java.lang.annotation.*;

/**
 * OpenAPIHiddenClass.
 * <p>
 * Use this on a class if you do not want it to be visible to OpenAPI generation module.
 *
 * @author Pierre Adam
 * @since 24.06.12
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OpenAPIHiddenClass {
}
