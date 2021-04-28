package com.merimdigitalmedia.underflow.converters.standards;

import com.merimdigitalmedia.underflow.converters.IConverter;

/**
 * DoubleConverter.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class DoubleConverter implements IConverter<Double> {

    @Override
    public Double bind(final String representation) {
        return Double.valueOf(representation);
    }

    @Override
    public String unbind(final Double object) {
        return object.toString();
    }

    @Override
    public Class<Double> getBackedType() {
        return Double.class;
    }
}
