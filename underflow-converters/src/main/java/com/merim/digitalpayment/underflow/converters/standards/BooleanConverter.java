package com.merim.digitalpayment.underflow.converters.standards;

import com.merim.digitalpayment.underflow.converters.IConverter;

/**
 * BooleanConverter.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class BooleanConverter implements IConverter<Boolean> {

    @Override
    public Boolean bind(final String representation) {
        if (representation.equalsIgnoreCase("true") || representation.equalsIgnoreCase("false")) {
            return Boolean.valueOf(representation);
        }
        throw new IllegalArgumentException("Not a boolean");
    }

    @Override
    public String unbind(final Boolean object) {
        return object.toString();
    }

    @Override
    public Class<Boolean> getBackedType() {
        return Boolean.class;
    }
}
