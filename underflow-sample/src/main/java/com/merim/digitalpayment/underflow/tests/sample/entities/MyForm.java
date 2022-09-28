package com.merim.digitalpayment.underflow.tests.sample.entities;

import com.merim.digitalpayment.underflow.web.forms.Form;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;

/**
 * TestForm.
 *
 * @author Pierre Adam
 * @since 21.10.05
 */
public class MyForm implements Form {

    /**
     * The Name.
     */
    private String name;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    @Override
    public void accept(final HttpServerExchange exchange, final FormData formData) throws Exception {
        this.name = formData.get("name").getFirst().getValue();
    }
}
