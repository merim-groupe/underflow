package com.merimdigitalmedia.underflow.entities;

import java.util.List;

/**
 * FormError.
 *
 * @author Pierre Adam
 * @since 22.08.02
 */
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

    /**
     * Gets form errors.
     *
     * @return the form errors
     */
    public List<FormError> getFormErrors() {
        return this.formErrors;
    }
}
