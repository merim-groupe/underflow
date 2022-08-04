package com.merim.digitalpayment.underflow.converters.standards;

import com.merim.digitalpayment.underflow.converters.IConverter;

import java.util.UUID;

/**
 * UUIDConverter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class UUIDConverter implements IConverter<UUID> {

    @Override
    public UUID bind(final String representation) {
        return UUID.fromString(representation);
    }

    @Override
    public String unbind(final UUID object) {
        return object.toString();
    }

    @Override
    public Class<UUID> getBackedType() {
        return UUID.class;
    }
}
