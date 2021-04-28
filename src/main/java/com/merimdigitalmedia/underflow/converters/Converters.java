package com.merimdigitalmedia.underflow.converters;

import com.merimdigitalmedia.underflow.converters.standards.*;

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
     * @param pClass the p class
     * @param value  the value
     * @return the object
     */
    public static Object convert(final Class<?> pClass,
                                 final String value) {
        if (Converters.converters.containsKey(pClass)) {
            return Converters.converters.get(pClass).bind(value);
        } else {
            for (final Class<?> aClass : Converters.converters.keySet()) {
                if (pClass.isAssignableFrom(aClass)) {
                    return Converters.converters.get(aClass).bind(value);
                }
            }
        }

        throw new RuntimeException("Unable to find a suitable converter !");
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
