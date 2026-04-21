package com.merim.digitalpayment.underflow.api.forms.errors;

import lombok.Getter;
import lombok.Setter;

/**
 * ServerError.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
@Getter
@Setter
public class ApiServerError {

    /**
     * The Request id.
     */
    private String requestUid;

    /**
     * The Type.
     */
    private String type;

    /**
     * The Message.
     */
    private String message;

    /**
     * The Message.
     */
    private String cause;

    /**
     * Instantiates a new Server error.
     */
    public ApiServerError() {
    }
}
