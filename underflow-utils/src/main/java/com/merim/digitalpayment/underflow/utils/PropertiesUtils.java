package com.merim.digitalpayment.underflow.utils;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

/**
 * PropertiesUtils.
 *
 * @author Pierre Adam
 * @since 25.03.06
 */
public class PropertiesUtils {

    /**
     * Load the properties from the resources.
     *
     * @param loader       the loader
     * @param resourcePath the resource path
     * @return the optional
     * @throws IOException the io exception
     */
    public static Optional<Properties> load(final Object loader, final String resourcePath) throws IOException {
        return PropertiesUtils.load(loader.getClass(), resourcePath);
    }

    /**
     * Load optional.
     *
     * @param loaderClass  the loader class
     * @param resourcePath the resource path
     * @return the optional
     * @throws IOException the io exception
     */
    public static Optional<Properties> load(final Class<?> loaderClass, final String resourcePath) throws IOException {
        InputStream resourceAsStream = loaderClass.getClassLoader().getResourceAsStream(resourcePath);

        if (resourceAsStream == null) {
            resourceAsStream = loaderClass.getResourceAsStream(resourcePath);
        }

        if (resourceAsStream != null) {
            final Properties value = new Properties();
            value.load(resourceAsStream);
            return Optional.of(value);
        }

        return Optional.empty();
    }

    /**
     * Load the properties from the filesystem.
     *
     * @param filesystemPath the filesystem path
     * @return the optional
     * @throws FileNotFoundException is the file does not exist
     * @throws IOException           the io exception
     */
    public static Optional<Properties> load(final String filesystemPath) throws IOException {
        final File file = new File(filesystemPath);

        if (!file.exists()) {
            return Optional.empty();
        }

        try (final FileInputStream fis = new FileInputStream(file)) {
            final Properties properties = new Properties();
            properties.load(fis);
            return Optional.of(properties);
        }
    }

    /**
     * Tries loading from the filesystem and if fails fallback to loading from the ressources.
     *
     * @param path   the path
     * @param loader the loader
     * @return the optional
     */
    public static Optional<Properties> autoLoad(final String path, final Class<?> loader) throws IOException {
        return PropertiesUtils.load(path, loader, path);
    }

    /**
     * Auto load optional.
     *
     * @param path   the path
     * @param loader the loader
     * @return the optional
     */
    public static Optional<Properties> autoLoad(final String path, final Object loader) throws IOException {
        return PropertiesUtils.load(path, loader, path);
    }

    /**
     * Tries loading from the filesystem and if fails fallback to loading from the ressources.
     *
     * @param filesystemPath the filesystem path
     * @param loader         the loader
     * @param resourcesPath  the resources path
     * @return the optional
     */
    public static Optional<Properties> load(final String filesystemPath, final Class<?> loader, final String resourcesPath) throws IOException {
        Optional<Properties> properties = Optional.empty();

        try {
            properties = PropertiesUtils.load(filesystemPath);
        } catch (final FileNotFoundException ignore) {
        }

        if (properties.isEmpty()) {
            properties = PropertiesUtils.load(loader, resourcesPath);
        }

        return properties;
    }

    /**
     * Load optional.
     *
     * @param filesystemPath the filesystem path
     * @param loader         the loader
     * @param resourcesPath  the resources path
     * @return the optional
     */
    public static Optional<Properties> load(final String filesystemPath, final Object loader, final String resourcesPath) throws IOException {
        return PropertiesUtils.load(filesystemPath, loader.getClass(), resourcesPath);
    }
}
