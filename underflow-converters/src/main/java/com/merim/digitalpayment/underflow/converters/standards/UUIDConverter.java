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
        if (representation == null) {
            return null;
        }

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

    @Override
    public String getSyntax() {
        return "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
    }
}
