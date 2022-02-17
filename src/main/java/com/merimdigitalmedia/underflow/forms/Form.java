package com.merimdigitalmedia.underflow.forms;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;

/**
 * Form.
 *
 * @author Pierre Adam
 * @since 21.10.05
 */
public interface Form {

    void accept(final HttpServerExchange exchange, final FormData formData) throws Exception;
}
