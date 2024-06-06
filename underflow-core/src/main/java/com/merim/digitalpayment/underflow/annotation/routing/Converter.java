package com.merim.digitalpayment.underflow.annotation.routing;

import com.merim.digitalpayment.underflow.converters.IConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * QueryConverter.
 *
 * @author Pierre Adam
 * @since 23.03.31
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Converter {

    /**
     * Value class.
     *
     * @return the class
     */
    Class<? extends IConverter<?>> value();
}
