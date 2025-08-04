package com.merim.digitalpayment.underflow.i18n;

import com.merim.digitalpayment.underflow.i18n.cookie.I18nCookie;
import com.merim.digitalpayment.underflow.i18n.messageformat.AdvancedMessageFormat;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * I18n.
 *
 * @author Pierre Adam
 * @since 25.03.05
 */
public class I18n {

    /**
     * The Message buffer.
     */
    private final List<I18nSource> i18nSources;

    /**
     * Instantiates a new 18 n.
     */
    public I18n() {
        this.i18nSources = new LinkedList<>();
    }

    public static void setDefaultLocale(final Locale locale) {
        Locale.setDefault(locale);
        I18nCookie.setDefaultLocale(locale);
    }

    /**
     * Add an i18n source.
     *
     * @param i18nSource the 18 n source
     * @return the 18 n
     */
    public I18n addI18nSource(final I18nSource i18nSource) {
        this.i18nSources.add(i18nSource);

        return this;
    }

    /**
     * Has locale boolean.
     *
     * @param locale the locale
     * @return the boolean
     */
    public boolean hasLocale(final Locale locale) {
        return this.fromSources(s -> s.hasLocale(locale));
    }

    /**
     * Has message boolean.
     *
     * @param key the key
     * @return the boolean
     */
    public boolean hasMessage(final String key) {
        return this.fromSources(s -> s.hasMessage(key));
    }

    /**
     * Has message boolean.
     *
     * @param locale the locale
     * @param key    the key
     * @return the boolean
     */
    public boolean hasMessage(final Locale locale, final String key) {
        return this.fromSources(s -> s.hasMessage(locale, key));
    }

    /**
     * Get string.
     *
     * @param locale the locale
     * @param key    the key
     * @return the string
     */
    public String get(final Locale locale, final String key) {
        return this.getOptional(locale, key).orElse(key);
    }

    /**
     * Get string.
     *
     * @param locale the locale
     * @param key    the key
     * @param args   the args
     * @return the string
     */
    public String get(final Locale locale, final String key, final Object... args) {
        return this.getOptional(locale, key, args).orElse(key);
    }

    /**
     * Get string.
     *
     * @param locale the locale
     * @param key    the key
     * @param args   the args
     * @return the string
     */
    public String get(final Locale locale, final String key, final Map<String, Object> args) {
        return this.getOptional(locale, key, args).orElse(key);
    }

    /**
     * Gets optional.
     *
     * @param locale the locale
     * @param key    the key
     * @return the optional
     */
    public Optional<String> getOptional(final Locale locale, final String key) {
        return this.fromSources(s -> s.hasMessage(locale, key), s -> s.getMessage(locale, key));
    }

    /**
     * Gets optional.
     *
     * @param locale the locale
     * @param key    the key
     * @param args   the args
     * @return the optional
     */
    public Optional<String> getOptional(final Locale locale, final String key, final Object... args) {
        return this.getOptional(locale, key).map(message -> AdvancedMessageFormat.format(message, args));
    }

    /**
     * Gets optional.
     *
     * @param locale the locale
     * @param key    the key
     * @param args   the args
     * @return the optional
     */
    public Optional<String> getOptional(final Locale locale, final String key, final Map<String, Object> args) {
        return this.getOptional(locale, key).map(message -> AdvancedMessageFormat.format(message, args));
    }

    /**
     * Gets or default.
     *
     * @param locale       the locale
     * @param key          the key
     * @param defaultValue the default value
     * @return the or default
     */
    public String getOrDefault(final Locale locale, final String key, final String defaultValue) {
        return this.getOptional(locale, key).orElse(defaultValue);
    }

    /**
     * Gets or default.
     *
     * @param locale       the locale
     * @param key          the key
     * @param defaultValue the default value
     * @param args         the args
     * @return the or default
     */
    public String getOrDefault(final Locale locale, final String key, final String defaultValue, final Object... args) {
        return this.getOptional(locale, key, args).orElse(defaultValue);
    }

    /**
     * Gets or default.
     *
     * @param locale       the locale
     * @param key          the key
     * @param defaultValue the default value
     * @param args         the args
     * @return the or default
     */
    public String getOrDefault(final Locale locale, final String key, final String defaultValue, final Map<String, Object> args) {
        return this.getOptional(locale, key, args).orElse(defaultValue);
    }

    /**
     * Gets localized message.
     *
     * @param locale the locale
     * @return the localized message
     */
    public LocalizedMessage getLocalizedMessage(final Locale locale) {
        return new LocalizedMessageImpl(this, locale);
    }

    /**
     * From sources optional.
     *
     * @param <R>       the type parameter
     * @param predicate the predicate
     * @param get       the get
     * @return the optional
     */
    private <R> Optional<R> fromSources(final Predicate<I18nSource> predicate, final Function<I18nSource, Optional<R>> get) {
        for (final I18nSource i18nSource : this.i18nSources) {
            if (predicate.test(i18nSource)) {
                return get.apply(i18nSource);
            }
        }

        return Optional.empty();
    }

    /**
     * From sources boolean.
     *
     * @param predicate the predicate
     * @return the boolean
     */
    private boolean fromSources(final Predicate<I18nSource> predicate) {
        return this.fromSources(predicate, s -> Optional.of(true)).orElse(false);
    }
}
