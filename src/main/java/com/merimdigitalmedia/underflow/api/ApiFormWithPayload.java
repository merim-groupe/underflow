package com.merimdigitalmedia.underflow.api;

import java.util.Optional;

/**
 * ApiForm.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 21.08.05
 */
public interface ApiFormWithPayload<T> {

    /**
     * Validate the form. If a server error is returned, the server will return an error.
     * If the form is valid, null must be returned.
     *
     * @param payload the payload
     * @return a server error or null
     */
    Optional<ServerError> isValid(T payload);
}
