package com.merimdigitalmedia.underflow.converters.standards;

import com.merimdigitalmedia.underflow.converters.IConverter;

import java.math.BigInteger;

/**
 * BigIntegerConverter.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class BigIntegerConverter implements IConverter<BigInteger> {

    @Override
    public BigInteger bind(final String representation) {
        return new BigInteger(representation);
    }

    @Override
    public String unbind(final BigInteger object) {
        return object.toString();
    }

    @Override
    public Class<BigInteger> getBackedType() {
        return BigInteger.class;
    }
}
