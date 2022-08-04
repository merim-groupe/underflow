package com.merim.digitalpayment.underflow.web.forms;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;

/**
 * Form.
 *
 * @author Pierre Adam
 * @since 21.10.05
 */
public interface Form {

    /**
     * Accept.
     *
     * @param exchange the exchange
     * @param formData the form data
     * @throws Exception the exception
     */
    void accept(final HttpServerExchange exchange, final FormData formData) throws Exception;
}
