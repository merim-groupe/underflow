package com.merim.digitalpayment.underflow.api.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ApiBodyBindable.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
public interface ApiBodyBindable {

    /**
     * With valid sub form optional.
     *
     * @param prefix the prefix
     * @param form   the form
     * @return the optional
     */
    default List<FormError> withValidSubForm(final String prefix, final ApiForm form) {
        if (form == null) {
            return new ArrayList<>();
        }

        return form.isValid()
                .stream()
                .map(e -> new FormError(prefix + "." + e.getField(), e.getMessage()))
                .collect(Collectors.toList());
    }

    /**
     * With valid sub form list.
     *
     * @param <T>     the type parameter
     * @param prefix  the prefix
     * @param form    the form
     * @param payload the payload
     * @return the list
     */
    default <T> List<FormError> withValidSubForm(final String prefix, final ApiFormWithPayload<T> form, final T payload) {
        if (form == null) {
            return new ArrayList<>();
        }

        return form.isValid(payload)
                .stream()
                .map(e -> new FormError(prefix + "." + e.getField(), e.getMessage()))
                .collect(Collectors.toList());
    }
}
