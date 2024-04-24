package com.merim.digitalpayment.underflow.app;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Application.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
public class Application {

    /**
     * The constant mapper.
     */
    private static final ObjectMapper mapper;

    /**
     * The Instances.
     */
    private static final Map<Class<?>, Object> instances;

    /**
     * The constant mode.
     */
    private static Mode mode;

    static {
        mapper = new ObjectMapper();
        instances = new HashMap<>();

        Application.mapper.findAndRegisterModules();
        Application.mode = Mode.PROD;
    }

    /**
     * Init mode.
     *
     * @param aClass the a class
     */
    public static void initMode(final Class<?> aClass) {
        Application.initMode(Application.runFromJar(aClass) ? Mode.PROD : Mode.DEV);
    }

    /**
     * Init mode.
     *
     * @param mode the mode
     */
    public static void initMode(final Mode mode) {
        Application.mode = mode;
    }

    /**
     * Gets mode.
     *
     * @return the mode
     */
    public static Mode getMode() {
        return Application.mode;
    }

    /**
     * Sets mode.
     *
     * @param mode the mode
     */
    public static void setMode(final Mode mode) {
        Application.mode = mode;
    }

    /**
     * Gets mapper.
     *
     * @return the mapper
     */
    public static ObjectMapper getMapper() {
        return Application.mapper;
    }

    /**
     * Run from a jar.
     *
     * @param aClass the a class
     * @return true if running from a jar.
     */
    public static boolean runFromJar(final Class<?> aClass) {
        final String className = aClass.getName().replace('.', '/');
        final String classJar = aClass.getResource("/" + className + ".class").toString();

        return classJar.startsWith("jar:");
    }

    /**
     * Register.
     *
     * @param <T>      the type parameter
     * @param tClass   the t class
     * @param instance the instance
     */
    public static <T> void register(final Class<T> tClass, final T instance) {
        Application.instances.put(tClass, instance);
    }

    /**
     * Gets instance.
     *
     * @param <T>    the type parameter
     * @param tClass the t class
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(final Class<T> tClass) {
        return (T) Application.instances.get(tClass);
    }

    /**
     * Gets instance.
     *
     * @param <T>    the type parameter
     * @param tClass the t class
     * @return the instance
     */
    public static <T> Optional<T> getInstanceOptional(final Class<T> tClass) {
        return Optional.ofNullable(Application.getInstance(tClass));
    }

    /**
     * Reset the Application context to an initial state.
     * This should not be called in a standard operation.
     */
    public static void resetApplication() {
        Application.instances.clear();
    }
}
