package com.merim.digitalpayment.underflow.api.forms;

/**
 * FormError.
 *
 * @author Pierre Adam
 * @since 22.08.02
 */
public class FormError {

    /**
     * The Field.
     */
    private final String field;

    /**
     * The Message.
     */
    private final String message;

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

    /**
     * Gets field.
     *
     * @return the field
     */
    public String getField() {
        return this.field;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }
}
