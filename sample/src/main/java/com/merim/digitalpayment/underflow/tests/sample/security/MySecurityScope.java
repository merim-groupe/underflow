package com.merim.digitalpayment.underflow.tests.sample.security;

import com.merim.digitalpayment.underflow.security.annotations.Secured;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;

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
@Secured
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirement(name = Security.SECURITY_REQUIREMENT)
public @interface MySecurityScope {
    /**
     * Value string.
     *
     * @return the string
     */
    String value();
}
