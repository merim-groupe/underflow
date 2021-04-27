package com.merimdigitalmedia.underflow.converters;

import java.util.UUID;

/**
 * UUIDConverter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class UUIDConverter implements FlowConverter<UUID> {

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
