package com.merimdigitalmedia.underflow.converters;

/**
 * StringConverter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class StringConverter implements FlowConverter<String> {
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
