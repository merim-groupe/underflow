package com.merimdigitalmedia.underflow.api;

import java.util.Optional;

/**
 * ApiBodyBindable.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
public interface ApiBodyBindable {

    /**
     * As error optional.
     *
     * @param field  the field
     * @param reason the reason
     * @return the optional
     */
    default Optional<ServerError> asError(final String field, final String reason) {
        return Optional.of(new ServerError("Bad request", field, reason));
    }

    /**
     * With any optional.
     *
     * @param errors the errors
     * @return the optional
     */
    default Optional<ServerError> withAny(final Optional<ServerError>... errors) {
        for (final Optional<ServerError> error : errors) {
            if (error.isPresent()) {
                return error;
            }
        }
        return Optional.empty();
    }
}
