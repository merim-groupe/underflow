package com.merim.digitalpayment.underflow.sample.dto;

import com.merim.digitalpayment.underflow.i18n.LocalizedMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * SampleDTOWrapper.
 * <p>
 * This Class should not be converted to a Record.
 * Freemarker DTO do not properly work with records.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 25.03.19
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SampleDTOWrapper<T> {

    /**
     * The Data.
     */
    private Object data;

    /**
     * The Current url.
     */
    private String currentUrl;

    /**
     * The Localized message.
     */
    private LocalizedMessage messages;
}
