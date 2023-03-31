package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * Value string.
     *
     * @return the string
     */
    String value();

    /**
     * Required boolean.
     *
     * @return the boolean
     */
    boolean required() default false;

    /**
     * List property query list.
     *
     * @return the query list
     */
    QueryListProperty listProperty() default @QueryListProperty();

    /**
     * The default value.
     *
     * @return the default value
     */
    DefaultValue defaultValue() default @DefaultValue();

    /**
     * Query converter query converter.
     *
     * @return the query converter
     */
    QueryConverter converter() default @QueryConverter();
}
