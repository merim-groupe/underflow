package com.merim.digitalpayment.underflow.i18n.sources;

import com.merim.digitalpayment.underflow.i18n.I18nSource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * AbstractI18nSource.
 *
 * @param <T> the type parameter
 * @author Pierre Adam
 * @since 25.03.05
 */
public abstract class AbstractI18nSource<T> implements I18nSource {

    /**
     * The Locale messages.
     */
    protected Map<Locale, T> localeMessages;

    /**
     * Instantiates a new Abstract i 18 n source.
     */
    protected AbstractI18nSource() {
        this.localeMessages = new HashMap<>();
    }

    /**
     * Instantiates a new Abstract i 18 n source.
     *
     * @param localeMessages the locale messages
     */
    protected AbstractI18nSource(final Map<Locale, T> localeMessages) {
        // Create a copy of the map to avoid unexpected results
        this.localeMessages = new HashMap<>(localeMessages);
    }

    @Override
    public Collection<Locale> getAvailableLocales() {
        return this.localeMessages.keySet();
    }

    @Override
    public boolean hasLocale(final Locale locale) {
        return this.getAvailableLocales().contains(locale);
    }

    @Override
    public boolean hasMessage(final String key) {
        for (final Locale availableLocale : this.getAvailableLocales()) {
            if (this.hasMessage(availableLocale, key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasMessage(final Locale locale, final String key) {
        return this.getMessage(locale, key).isPresent();
    }
}
