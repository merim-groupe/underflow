package com.merim.digitalpayment.underflow.converters;

import com.merim.digitalpayment.underflow.converters.standards.*;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * FlowConverter.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@Slf4j
public class Converters {

    /**
     * The list of available converters.
     */
    private static final Map<Class<?>, IConverter<?>> converters;

    /**
     * The list of available converters.
     */
    private static final Map<Class<?>, IConverter<?>> runtimeConverters;

    static {
        converters = new HashMap<>();
        runtimeConverters = new HashMap<>();
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

    /**
     * Syntax string.
     *
     * @param pClass the p class
     * @return the string
     * @throws IllegalArgumentException is no suitable converter were found
     */
    public static String getSyntax(@NonNull final Class<?> pClass) throws IllegalArgumentException {
        if (Converters.converters.containsKey(pClass)) {
            return Converters.converters.get(pClass).getSyntax();
        } else if (pClass.isEnum()) {
            return Converters.converters.get(String.class).getSyntax();
        } else {
            for (final Class<?> aClass : Converters.converters.keySet()) {
                if (pClass.isAssignableFrom(aClass)) {
                    return Converters.converters.get(aClass).getSyntax();
                }
            }
        }

        throw new IllegalArgumentException("Unable to find a suitable converter !");
    }

    /**
     * Gets runtime converter.
     *
     * @param converterClass the converter class
     * @return the runtime converter
     */
    public static IConverter<?> getRuntimeConverter(final Class<? extends IConverter<?>> converterClass) {
        if (Converters.runtimeConverters.containsKey(converterClass)) {
            return Converters.runtimeConverters.get(converterClass);
        }

        try {
            synchronized (Converters.runtimeConverters) {
                final IConverter<?> converter = converterClass.getDeclaredConstructor().newInstance();

                Converters.runtimeConverters.put(converterClass, converter);

                return converter;
            }
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Converters.logger.error("Unable to instantiate the converter {}. Please add a default constructor or use Converters.registerRuntimeConverter()", converterClass.getCanonicalName());
            throw new RuntimeException(e);
        }
    }

    /**
     * Register runtime converter.
     *
     * @param converter the converter
     */
    public void registerRuntimeConverter(final IConverter<?> converter) {
        synchronized (Converters.runtimeConverters) {
            Converters.runtimeConverters.put(converter.getClass(), converter);
        }
    }
}
