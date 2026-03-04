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
     * The constant defaultVersion.
     */
    protected static String defaultVersion = "0.0";

    /**
     * Instantiates a new Version utils.
     */
    private UnderflowVersionUtils() {
    }

    /**
     * Load version.
     *
     * @param cls the class
     * @return the underflow version utils
     */
    public static void loadVersion(final Class<?> cls) {
        UnderflowVersionUtils.version = cls.getPackage().getImplementationVersion();
    }

    /**
     * Load version.
     *
     * @param object the object
     * @return the underflow version utils
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
        return Optional.ofNullable(UnderflowVersionUtils.version).orElse(UnderflowVersionUtils.defaultVersion);
    }

    /**
     * Sets default.
     *
     * @param defaultVersion the default version
     */
    public static void setDefault(final String defaultVersion) {
        UnderflowVersionUtils.defaultVersion = defaultVersion;
    }
}
