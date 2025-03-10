package com.merim.digitalpayment.underflow.i18n;

import java.util.Locale;
import java.util.Optional;

/**
 * Message.
 *
 * @author Pierre Adam
 * @since 25.03.05
 */
public interface LocalizedMessage {

    /**
     * Get string.
     *
     * @param key the key
     * @return the string
     */
    String get(final String key);

    /**
     * Get string.
     *
     * @param key  the key
     * @param args the args
     * @return the string
     */
    String get(final String key, final Object... args);

    /**
     * Get string.
     *
     * @param key the key
     * @return the string
     */
    Optional<String> getOptional(final String key);

    /**
     * Get string.
     *
     * @param key  the key
     * @param args the args
     * @return the string
     */
    Optional<String> getOptional(final String key, final Object... args);

    /**
     * Gets or default.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @return the or default
     */
    String getOrDefault(final String key, final String defaultValue);

    /**
     * Gets or default.
     *
     * @param key          the key
     * @param defaultValue the default value
     * @param args         the args
     * @return the or default
     */
    String getOrDefault(final String key, final String defaultValue, final Object... args);

    /**
     * Gets locale.
     *
     * @return the locale
     */
    Locale getLocale();
}
