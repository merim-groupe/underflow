package com.merim.digitalpayment.underflow.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * UnderflowStartupArgs.
 *
 * @author Pierre Adam
 * @since 25.07.31
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StartupArgs {

    /**
     * The test class.
     *
     * @return the class
     */
    String[] value();
}
