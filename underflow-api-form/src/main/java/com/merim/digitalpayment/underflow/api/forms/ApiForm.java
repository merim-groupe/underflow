package com.merim.digitalpayment.underflow.api.forms;

import java.util.List;

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
    List<FormError> isValid();
}
