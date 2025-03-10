package com.merim.digitalpayment.underflow.i18n;

import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

/**
 * I18nSource.
 *
 * @author Pierre Adam
 * @since 25.03.05
 */
public interface I18nSource {

    /**
     * Gets available locales.
     *
     * @return the available locales
     */
    Collection<Locale> getAvailableLocales();

    /**
     * Has locale boolean.
     *
     * @param locale the locale
     * @return the boolean
     */
    boolean hasLocale(Locale locale);

    /**
     * Has message boolean.
     *
     * @param key the key
     * @return the boolean
     */
    boolean hasMessage(String key);

    /**
     * Has message boolean.
     *
     * @param locale the locale
     * @param key    the key
     * @return the boolean
     */
    boolean hasMessage(Locale locale, String key);

    /**
     * Gets message.
     *
     * @param locale the locale
     * @param key    the key
     * @return the message
     */
    Optional<String> getMessage(Locale locale, String key);
}
