package com.merim.digitalpayment.underflow.converters;

/**
 * NoConverter.
 *
 * @author Pierre Adam
 * @since 23.03.31
 */
public final class NoConverter implements IConverter<String> {

    @Override
    public String bind(final String representation) {
        return null;
    }

    @Override
    public String unbind(final String object) {
        return null;
    }

    @Override
    public Class<String> getBackedType() {
        return null;
    }
}
