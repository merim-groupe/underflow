package com.merim.digitalpayment.underflow.utils;

import java.util.Optional;

/**
 * VersionUtil.
 *
 * @author Pierre Adam
 * @since 26.03.03
 */
public class UnderflowVersionUtils {

    /**
     * The Version.
     */
    protected static String version;

    /**
     * Instantiates a new Version utils.
     */
    private UnderflowVersionUtils() {
    }

    /**
     * Load version.
     *
     * @param cls the class
     */
    public static void loadVersion(final Class<?> cls) {
        UnderflowVersionUtils.version = cls.getPackage().getImplementationVersion();
    }

    /**
     * Load version.
     *
     * @param object the object
     */
    public static void loadVersion(final Object object) {
        UnderflowVersionUtils.version = object.getClass().getPackage().getImplementationVersion();
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public static String getVersion() {
        return Optional.ofNullable(UnderflowVersionUtils.version).orElse("0.0");
    }
}
