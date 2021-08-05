package com.merimdigitalmedia.underflow.api.entities;

/**
 * ApiForm.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
public abstract class ApiForm {

    /**
     * Validate the form. If a server error is returned, the server will return an error.
     * If the form is valid, null must be returned.
     *
     * @return a server error or null
     */
    public abstract ServerError isValid();
}
