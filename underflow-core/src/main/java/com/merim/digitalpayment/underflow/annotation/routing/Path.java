package com.merim.digitalpayment.underflow.annotation.routing;

import java.lang.annotation.*;

/**
 * Path.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@Repeatable(Paths.class)
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
    /**
     * Value string.
     *
     * @return the string
     */
    String value();

    /**
     * Ignore case boolean.
     *
     * @return the boolean
     */
    boolean ignoreCase() default false;

    /**
     * Lazy match will not try to match the entire request.
     * Example:
     * <pre>{@code
     *  @Path(value="/api", lazyMatch=true)
     *   Will match with /api/foo, /api/bar, etc
     *  @Path("/api")
     *   Will only match with /api. /api/foo wont be a match.
     * }</pre>
     *
     * @return the boolean
     */
    boolean lazyMatch() default false;
}
