package com.merimdigitalmedia.underflow.converters.standards;

import com.merimdigitalmedia.underflow.converters.IConverter;

/**
 * LongConverter.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class LongConverter implements IConverter<Long> {

    @Override
    public Long bind(final String representation) {
        return Long.valueOf(representation);
    }

    @Override
    public String unbind(final Long object) {
        return object.toString();
    }

    @Override
    public Class<Long> getBackedType() {
        return Long.class;
    }
}
