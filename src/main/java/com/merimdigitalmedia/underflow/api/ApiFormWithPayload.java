package com.merimdigitalmedia.underflow.api;

import java.util.Optional;

/**
 * ApiForm.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 21.08.05
 */
public interface ApiFormWithPayload<T> extends ApiBodyBindable {

    /**
     * Validate the form. If a server error is returned, the server will return an error.
     * If the form is valid, null must be returned.
     *
     * @param payload the payload
     * @return a server error or null
     */
    Optional<ServerError> isValid(T payload);

    /**
     * With valid sub form optional.
     *
     * @param prefix the prefix
     * @param form   the form
     * @return the optional
     */
    default <U> Optional<ServerError> withValidSubForm(final String prefix, final ApiFormWithPayload<U> form, final U payload) {
        final Optional<ServerError> optionalError = form.isValid(payload);

        if (optionalError.isPresent()) {
            final ServerError error = optionalError.get();
            return this.asError(prefix + "." + error.getMessage(), error.getMessage());
        }

        return optionalError;
    }
}
