package com.merim.digitalpayment.underflow.converters.standards;

import com.merim.digitalpayment.underflow.converters.IConverter;

/**
 * StringConverter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class StringConverter implements IConverter<String> {
    @Override
    public String bind(final String representation) {
        return representation;
    }

    @Override
    public String unbind(final String object) {
        return object;
    }

    @Override
    public Class<String> getBackedType() {
        return String.class;
    }
}
