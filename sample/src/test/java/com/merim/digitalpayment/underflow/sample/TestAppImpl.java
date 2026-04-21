package com.merim.digitalpayment.underflow.sample;

import com.merim.digitalpayment.underflow.test.server.UnderflowTestApplicationImpl;

/**
 * TestApp.
 *
 * @author Pierre Adam
 * @since 24.04.22
 */
public class TestAppImpl extends UnderflowTestApplicationImpl<MainSample> {

    /**
     * Instantiates a new Underflow test application.
     */
    public TestAppImpl() {
        super(MainSample.class);
    }
}
