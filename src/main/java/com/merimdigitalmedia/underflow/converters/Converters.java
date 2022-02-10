package com.merimdigitalmedia.underflow.converters;

import com.merimdigitalmedia.underflow.converters.standards.BigDecimalConverter;
import com.merimdigitalmedia.underflow.converters.standards.BigIntegerConverter;
import com.merimdigitalmedia.underflow.converters.standards.BooleanConverter;
import com.merimdigitalmedia.underflow.converters.standards.DoubleConverter;
import com.merimdigitalmedia.underflow.converters.standards.FloatConverter;
import com.merimdigitalmedia.underflow.converters.standards.IntegerConverter;
import com.merimdigitalmedia.underflow.converters.standards.LongConverter;
import com.merimdigitalmedia.underflow.converters.standards.StringConverter;
import com.merimdigitalmedia.underflow.converters.standards.UUIDConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * FlowConverter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class Converters {

    /**
     * The list of available converters.
     */
    private static final Map<Class<?>, IConverter<?>> converters;

    static {
        converters = new HashMap<>();
        Converters.addConverter(new BigDecimalConverter());
        Converters.addConverter(new BigIntegerConverter());
        Converters.addConverter(new BooleanConverter());
        Converters.addConverter(new DoubleConverter());
        Converters.addConverter(new FloatConverter());
        Converters.addConverter(new IntegerConverter());
        Converters.addConverter(new LongConverter());
        Converters.addConverter(new StringConverter());
        Converters.addConverter(new UUIDConverter());
    }

    /**
     * Convert a value into the proper Object.
     *
     * @param <T>    the type parameter
     * @param pClass the p class
     * @param value  the value
     * @return the object
     */
    public static <T> T convert(final Class<T> pClass,
                                final String value) {
        if (value == null) {
            return null;
        }
        if (Converters.converters.containsKey(pClass)) {
            return pClass.cast(Converters.converters.get(pClass).bind(value));
        } else if (pClass.isEnum()) {
            return Converters.enumValueOf(pClass, value);
        } else {
            for (final Class<?> aClass : Converters.converters.keySet()) {
                if (pClass.isAssignableFrom(aClass)) {
                    return pClass.cast(Converters.converters.get(aClass).bind(value));
                }
            }
        }

        throw new RuntimeException("Unable to find a suitable converter !");
    }

    /**
     * Enum value of t.
     *
     * @param <T>    the type parameter
     * @param tClass the t class
     * @param value  the value
     * @return the t
     */
    private static <T> T enumValueOf(final Class<T> tClass, final String value) {
        final T[] enumConstants = tClass.getEnumConstants();
        for (final T enumConstant : enumConstants) {
            final String name = ((Enum<?>) enumConstant).name();
            if (value.equals(name)) {
                return enumConstant;
            }
        }
        return null;
    }

    /**
     * Add a converter to the list of available converters.
     *
     * @param flowConverter the flow converter
     */
    public static void addConverter(final IConverter<?> flowConverter) {
        Converters.converters.put(flowConverter.getBackedType(), flowConverter);
    }
}
