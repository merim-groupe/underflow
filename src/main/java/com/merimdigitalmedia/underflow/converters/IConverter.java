package com.merimdigitalmedia.underflow.converters;

/**
 * FlowConverter.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 21.04.27
 */
public interface IConverter<T> {

    /**
     * Bind t.
     *
     * @param representation the representation
     * @return the t
     */
    T bind(String representation);

    /**
     * Unbind string.
     *
     * @param object the object
     * @return the string
     */
    String unbind(T object);

    /**
     * Gets backed type.
     *
     * @return the backed type
     */
    Class<T> getBackedType();
}
