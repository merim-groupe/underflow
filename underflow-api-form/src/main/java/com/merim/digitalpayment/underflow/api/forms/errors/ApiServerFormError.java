package com.merim.digitalpayment.underflow.api.forms.errors;

import com.merim.digitalpayment.underflow.api.forms.FormError;

import java.util.List;

/**
 * FormError.
 *
 * @author Pierre Adam
 * @since 22.08.02
 */
public class ApiServerFormError extends ApiServerError {

    /**
     * The Form errors.
     */
    private List<FormError> formErrors;

    /**
     * Instantiates a new Server form error.
     */
    public ApiServerFormError() {
    }

    /**
     * Gets form errors.
     *
     * @return the form errors
     */
    public List<FormError> getFormErrors() {
        return this.formErrors;
    }

    /**
     * Sets form errors.
     *
     * @param formErrors the form errors
     */
    public void setFormErrors(final List<FormError> formErrors) {
        this.formErrors = formErrors;
    }
}
