package com.merim.digitalpayment.underflow.api.forms.errors;

import com.merim.digitalpayment.underflow.api.forms.FormError;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * FormError.
 *
 * @author Pierre Adam
 * @since 22.08.02
 */
@Getter
@Setter
public class ApiServerFormError extends ApiServerError {

    /**
     * The Form errors.
     */
    private List<FormError> formErrors;

    /**
     * Instantiates a new Server form error.
     */
    public ApiServerFormError() {
    }
}
