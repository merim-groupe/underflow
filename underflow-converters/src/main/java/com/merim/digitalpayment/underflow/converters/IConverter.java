package com.merim.digitalpayment.underflow.converters;

/**
 * An interface for converting objects of type T to and from their String representations.
 * This enables bidirectional transformation between a String representation and the backed type.
 *
 * @param <T> The type of object this converter handles.
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

    /**
     * Gets syntax.
     *
     * @return the syntax
     */
    default String getSyntax() {
        return "[^/]+";
    }
}
