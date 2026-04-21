package com.merim.digitalpayment.underflow.entities;

import com.merim.digitalpayment.underflow.api.forms.FormError;
import lombok.Getter;

import java.util.List;

/**
 * FormError.
 *
 * @author Pierre Adam
 * @since 22.08.02
 */
@Getter
public class ServerFormError extends ServerError {

    /**
     * The Form errors.
     */
    private final List<FormError> formErrors;

    /**
     * Instantiates a new Server form error.
     *
     * @param formErrors the form errors
     */
    public ServerFormError(final List<FormError> formErrors) {
        super("Bad request", "Invalid form", "The form contains errors.");
        this.formErrors = formErrors;
    }
}
