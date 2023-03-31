package com.merim.digitalpayment.underflow.annotation.routing;

import com.merim.digitalpayment.underflow.converters.IConverter;
import com.merim.digitalpayment.underflow.converters.NoConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * QueryConverter.
 *
 * @author Pierre Adam
 * @since 23.03.31
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryConverter {

    Class<? extends IConverter<?>> value() default NoConverter.class;
}
