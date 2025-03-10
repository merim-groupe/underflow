package com.merim.digitalpayment.underflow.i18n;

import java.util.Locale;
import java.util.Optional;

/**
 * LocalizedMessageImpl.
 *
 * @author Pierre Adam
 * @since 25.03.05
 */
public class LocalizedMessageImpl implements LocalizedMessage {

    /**
     * The 18 n.
     */
    private final I18n i18n;

    /**
     * The Locale.
     */
    private final Locale locale;

    /**
     * Instantiates a new Localized message.
     *
     * @param i18n   the 18 n
     * @param locale the locale
     */
    public LocalizedMessageImpl(final I18n i18n, final Locale locale) {
        this.i18n = i18n;
        this.locale = locale;
    }

    @Override
    public String get(final String key) {
        return this.i18n.get(this.locale, key);
    }

    @Override
    public String get(final String key, final Object... args) {
        return this.i18n.get(this.locale, key, args);
    }

    @Override
    public Optional<String> getOptional(final String key) {
        return this.i18n.getOptional(this.locale, key);
    }

    @Override
    public Optional<String> getOptional(final String key, final Object... args) {
        return this.i18n.getOptional(this.locale, key, args);
    }

    @Override
    public String getOrDefault(final String key, final String defaultValue) {
        return this.i18n.getOrDefault(this.locale, key, defaultValue);
    }

    @Override
    public String getOrDefault(final String key, final String defaultValue, final Object... args) {
        return this.i18n.getOrDefault(this.locale, key, defaultValue, args);
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }
}
