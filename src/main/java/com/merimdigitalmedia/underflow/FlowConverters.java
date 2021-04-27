package com.merimdigitalmedia.underflow;

import com.merimdigitalmedia.underflow.converters.FlowConverter;
import com.merimdigitalmedia.underflow.converters.IntegerConverter;
import com.merimdigitalmedia.underflow.converters.StringConverter;
import com.merimdigitalmedia.underflow.converters.UUIDConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * FlowConverter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class FlowConverters {

    private static final Map<Class<?>, FlowConverter<?>> converters;

    static {
        converters = new HashMap<>();
        FlowConverters.addConverter(new StringConverter());
        FlowConverters.addConverter(new IntegerConverter());
        FlowConverters.addConverter(new UUIDConverter());
    }

    public static Object convert(final Class<?> pClass,
                                 final String value) {
        if (FlowConverters.converters.containsKey(pClass)) {
            return FlowConverters.converters.get(pClass).bind(value);
        } else {
            for (final Class<?> aClass : FlowConverters.converters.keySet()) {
                if (pClass.isAssignableFrom(aClass)) {
                    return FlowConverters.converters.get(aClass).bind(value);
                }
            }
        }

        throw new RuntimeException("Unable to find a suitable converter !");
    }

    public static void addConverter(final FlowConverter<?> flowConverter) {
        FlowConverters.converters.put(flowConverter.getBackedType(), flowConverter);
    }
}
