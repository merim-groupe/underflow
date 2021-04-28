package com.merimdigitalmedia.underflow.converters.standards;

import com.merimdigitalmedia.underflow.converters.IConverter;

import java.math.BigDecimal;

/**
 * BigDecimalConverter.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class BigDecimalConverter implements IConverter<BigDecimal> {

    @Override
    public BigDecimal bind(final String representation) {
        return new BigDecimal(representation);
    }

    @Override
    public String unbind(final BigDecimal object) {
        return object.toString();
    }

    @Override
    public Class<BigDecimal> getBackedType() {
        return BigDecimal.class;
    }
}
