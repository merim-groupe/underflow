package com.merimdigitalmedia.underflow.api;

import java.util.Optional;

/**
 * ApiForm.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
public interface ApiForm extends ApiBodyBindable {

    /**
     * Validate the form. If a server error is returned, the server will return an error.
     * If the form is valid, null must be returned.
     *
     * @return a server error or null
     */
    Optional<ServerError> isValid();

    /**
     * With valid sub form optional.
     *
     * @param prefix the prefix
     * @param form   the form
     * @return the optional
     */
    default Optional<ServerError> withValidSubForm(final String prefix, final ApiForm form) {
        final Optional<ServerError> optionalError = form.isValid();

        if (optionalError.isPresent()) {
            final ServerError error = optionalError.get();
            return this.asError(prefix + "." + error.getMessage(), error.getMessage());
        }

        return optionalError;
    }
}
