package com.merim.digitalpayment.underflow.i18n.sources;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * PropertiesSource.
 *
 * @author Pierre Adam
 * @since 25.03.05
 */
@Slf4j
public class PropertiesSource extends AbstractI18nSource<Properties> {

    /**
     * Instantiates a new Properties source.
     *
     * @param localeMessages the locale messages
     */
    PropertiesSource(final Map<Locale, Properties> localeMessages) {
        super(localeMessages);
    }

    /**
     * Builder properties source builder.
     *
     * @return the properties source builder
     */
    public static PropertiesSourceBuilder builder() {
        return new PropertiesSourceBuilder();
    }

    /**
     * From file properties source.
     *
     * @param filesystemPath the filesystem path
     * @return the properties source
     */
    public static Optional<Properties> fromFile(final String filesystemPath) {
        return PropertiesSource.fromFile(new File(filesystemPath));
    }

    /**
     * From file optional.
     *
     * @param file the file
     * @return the optional
     */
    public static Optional<Properties> fromFile(final File file) {
        if (!file.exists()) {
            return Optional.empty();
        }

        try (final FileInputStream fis = new FileInputStream(file)) {
            final Properties properties = new Properties();
            properties.load(fis);
            return Optional.of(properties);
        } catch (final IOException e) {
            PropertiesSource.logger.error("An error occurred while loading {} from filesystem", file.getPath());
            return Optional.empty();
        }
    }

    /**
     * Load properties from resource optional.
     *
     * @param classLoader  the class loader
     * @param resourcePath the resource path
     * @return the optional
     */
    public static Optional<Properties> loadPropertiesFromResource(final Class<?> classLoader,
                                                                  final String resourcePath) {
        InputStream resourceAsStream = classLoader.getClassLoader().getResourceAsStream(resourcePath);

        if (resourceAsStream == null) {
            resourceAsStream = classLoader.getResourceAsStream(resourcePath);
        }

        if (resourceAsStream != null) {
            final Properties value = new Properties();

            try {
                value.load(resourceAsStream);
            } catch (final IOException e) {
                PropertiesSource.logger.error("An error occurred while loading {} from {}", resourcePath, classLoader.getCanonicalName());
                return Optional.empty();
            }

            return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public Optional<String> getMessage(final Locale locale, final String key) {
        if (key == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.localeMessages.getOrDefault(locale, new Properties()).getProperty(key));
    }
}
