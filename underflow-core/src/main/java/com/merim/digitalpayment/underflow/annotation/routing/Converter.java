package com.merim.digitalpayment.underflow.annotation.routing;

import com.merim.digitalpayment.underflow.converters.IConverter;

import java.lang.annotation.*;

/**
 * QueryConverter.
 *
 * @author Pierre Adam
 * @since 23.03.31
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Converter {

    /**
     * Value class.
     *
     * @return the class
     */
    Class<? extends IConverter<?>> value();
}
