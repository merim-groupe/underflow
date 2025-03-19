package com.merim.digitalpayment.underflow.tests.sample.dto;

import com.merim.digitalpayment.underflow.i18n.LocalizedMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SampleDTOWrapper.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 25.03.19
 */
@Getter
@AllArgsConstructor
public class SampleDTOWrapper<T> {

    /**
     * The Data.
     */
    private final Object data;

    /**
     * The Current url.
     */
    private final String currentUrl;

    /**
     * The Localized message.
     */
    private final LocalizedMessage messages;
}
