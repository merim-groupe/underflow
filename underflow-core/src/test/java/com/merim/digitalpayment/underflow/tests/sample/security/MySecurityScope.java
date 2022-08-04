package com.merim.digitalpayment.underflow.tests.sample.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MySecurityScope.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MySecurityScope {
    String value();
}
