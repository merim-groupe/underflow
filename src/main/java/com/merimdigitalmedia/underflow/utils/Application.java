package com.merimdigitalmedia.underflow.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Application.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
public class Application {

    /**
     * The constant mode.
     */
    private static Mode mode;

    /**
     * The constant mapper.
     */
    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        Application.mapper.findAndRegisterModules();

        if (Application.runFromJar()) {
            Application.mode = Mode.PROD;
        } else {
            Application.mode = Mode.DEV;
        }
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
     * @return true if running from a jar.
     */
    public static boolean runFromJar() {
        final Class<Application> aClass = Application.class;
        final String className = aClass.getName().replace('.', '/');
        final String classJar = aClass.getResource("/" + className + ".class").toString();

        return classJar.startsWith("jar:");
    }
}
