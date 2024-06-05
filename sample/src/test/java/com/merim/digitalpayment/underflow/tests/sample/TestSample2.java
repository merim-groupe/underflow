package com.merim.digitalpayment.underflow.tests.sample;

import com.merim.digitalpayment.underflow.test.UnderflowTest;
import org.junit.jupiter.api.Test;

/**
 * TestSample.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
@UnderflowTest(TestAppImpl.class)
public class TestSample2 {

    @Test
    public void test() {
        System.out.println("Running test 2 !");
    }
}
