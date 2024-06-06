package com.merim.digitalpayment.underflow.test;

import com.merim.digitalpayment.underflow.test.server.UnderflowTestApplicationImpl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UnderflowTest.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
@ExtendWith(UnderflowTestExtension.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Tag("com.merim.digitalpayment.underflow.tests.UnderflowTest")
public @interface UnderflowTest {

    /**
     * The test class.
     *
     * @return the class
     */
    Class<? extends UnderflowTestApplicationImpl<?>> value();
}
