package com.merimdigitalmedia.underflow.converters.standards;

import com.merimdigitalmedia.underflow.converters.IConverter;

/**
 * FloatConverter.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class FloatConverter implements IConverter<Float> {

    @Override
    public Float bind(final String representation) {
        return Float.valueOf(representation);
    }

    @Override
    public String unbind(final Float object) {
        return object.toString();
    }

    @Override
    public Class<Float> getBackedType() {
        return Float.class;
    }
}
