package com.merim.digitalpayment.underflow.tests.sample.form;

import com.merim.digitalpayment.underflow.api.forms.ApiForm;
import com.merim.digitalpayment.underflow.api.forms.FormError;
import com.merim.digitalpayment.underflow.web.forms.Form;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;

import java.util.ArrayList;
import java.util.List;

/**
 * LoginForm.
 *
 * @author Pierre Adam
 * @since 23.04.14
 */
public class LoginForm implements Form, ApiForm {

    /**
     * The Name.
     */
    private String name;

    /**
     * The Scopes.
     */
    private List<String> scopes;

    /**
     * Instantiates a new Login form.
     */
    public LoginForm() {
        this.scopes = new ArrayList<>();
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Gets scopes.
     *
     * @return the scopes
     */
    public List<String> getScopes() {
        return this.scopes;
    }

    /**
     * Sets scopes.
     *
     * @param scopes the scopes
     */
    public void setScopes(final List<String> scopes) {
        this.scopes = scopes;
    }

    @Override
    public void accept(final HttpServerExchange exchange, final FormData formData) throws Exception {
        if (formData.contains("name")) {
            this.name = formData.getFirst("name").getValue();
        }

        if (formData.contains("scope")) {
            formData.get("scope").stream()
                    .map(FormData.FormValue::getValue)
                    .filter(value -> value != null && !value.isEmpty())
                    .forEach(this.scopes::add);
        }

        if (formData.contains("scope[]")) {
            formData.get("scope[]").stream()
                    .map(FormData.FormValue::getValue)
                    .filter(value -> value != null && !value.isEmpty())
                    .forEach(this.scopes::add);
        }

        if (this.isValid() != null) {
            throw new RuntimeException("Invalid form.");
        }
    }

    @Override
    public List<FormError> isValid() {
        final ArrayList<FormError> errors = new ArrayList<>();

        if (this.name == null || this.name.trim().isEmpty()) {
            errors.add(new FormError("name", "name is empty"));
        }

        return errors.isEmpty() ? null : errors;
    }
}
