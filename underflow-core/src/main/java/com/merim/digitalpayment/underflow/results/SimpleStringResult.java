package com.merim.digitalpayment.underflow.results;

import com.merim.digitalpayment.underflow.results.http.SenderHttpResult;

/**
 * StringResult.
 *
 * @author Pierre Adam
 * @since 24.06.03
 */
public class SimpleStringResult extends SenderHttpResult {

    /**
     * Instantiates a new Result.
     *
     * @param data the data
     */
    public SimpleStringResult(final String data) {
        super(200, sender -> sender.send(data == null ? "" : data));
    }
}
