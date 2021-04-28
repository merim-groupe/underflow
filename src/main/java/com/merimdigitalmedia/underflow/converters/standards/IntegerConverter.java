package com.merimdigitalmedia.underflow.converters.standards;

import com.merimdigitalmedia.underflow.converters.IConverter;

/**
 * IntegerConverter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class IntegerConverter implements IConverter<Integer> {

    @Override
    public Integer bind(final String representation) {
        return Integer.parseInt(representation);
    }

    @Override
    public String unbind(final Integer object) {
        return object.toString();
    }

    @Override
    public Class<Integer> getBackedType() {
        return Integer.class;
    }
}
