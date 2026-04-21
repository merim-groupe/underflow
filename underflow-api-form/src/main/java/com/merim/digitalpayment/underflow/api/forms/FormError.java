package com.merim.digitalpayment.underflow.api.forms;

import lombok.Getter;
import lombok.Setter;

/**
 * FormError.
 *
 * @author Pierre Adam
 * @since 22.08.02
 */
@Getter
@Setter
public class FormError {

    /**
     * The Field.
     */
    private String field;

    /**
     * The Message.
     */
    private String message;

    /**
     * Instantiates a new Form error.
     */
    public FormError() {
    }

    /**
     * Instantiates a new Form error.
     *
     * @param field   the field
     * @param message the message
     */
    public FormError(final String field, final String message) {
        this.field = field;
        this.message = message;
    }
}
