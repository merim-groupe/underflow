package com.merimdigitalmedia.underflow.api;

import java.util.Optional;

/**
 * ApiForm.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
public interface ApiForm {

    /**
     * Validate the form. If a server error is returned, the server will return an error.
     * If the form is valid, null must be returned.
     *
     * @return a server error or null
     */
    Optional<ServerError> isValid();
}
