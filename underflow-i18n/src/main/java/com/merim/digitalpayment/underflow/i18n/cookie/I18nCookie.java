package com.merim.digitalpayment.underflow.i18n.cookie;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.HeaderValues;
import lombok.NonNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * I18nCookie.
 *
 * @author Pierre Adam
 * @since 25.03.11
 */
public class I18nCookie {

    /**
     * The constant cookieName.
     */
    private static String cookieName = "lang";

    /**
     * The constant defaultLocale.
     */
    private static Locale defaultLocale = null;

    /**
     * The Allowed locale.
     */
    private static Set<Locale> allowedLocale;

    static {
        I18nCookie.allowedLocale = new HashSet<>();
    }

    /**
     * Gets cookie name.
     *
     * @return the cookie name
     */
    public static String getCookieName() {
        return I18nCookie.cookieName;
    }

    /**
     * Sets cookie name.
     *
     * @param cookieName the cookie name
     */
    public static void setCookieName(@NonNull final String cookieName) {
        I18nCookie.cookieName = cookieName;
    }

    /**
     * Sets default locale.
     *
     * @param defaultLocale the default locale
     */
    public static void setDefaultLocale(@NonNull final Locale defaultLocale) {
        I18nCookie.defaultLocale = defaultLocale;
    }

    /**
     * Add allowed locale.
     *
     * @param locale the locale
     */
    public static void addAllowedLocale(@NonNull final Locale locale) {
        I18nCookie.allowedLocale.add(locale);
    }

    /**
     * Resolve from allowed locales locale.
     *
     * @param locale the locale
     * @return the locale
     */
    public static Locale resolveFromAllowedLocalesOrDefault(final Locale locale) {
        // Iterate through the list of preferred locales and return the first match that is explicitly allowed
        if (I18nCookie.allowedLocale.contains(locale)) {
            return locale;
        }

        // If no exact match is found, look for matches based solely on language
        for (final Locale allowedLocale : I18nCookie.allowedLocale) {
            if (allowedLocale.getLanguage().equals(locale.getLanguage())) {
                return allowedLocale;
            }
        }

        return I18nCookie.defaultLocale;
    }

    /**
     * Create cookie cookie.
     *
     * @param locale the locale
     * @return the cookie
     */
    public static Cookie createCookie(final Locale locale) {
        return new CookieImpl(I18nCookie.cookieName, locale.toLanguageTag());
    }

    /**
     * Resolve and set cookie locale.
     *
     * @param exchange  the exchange
     * @param setCookie the set cookie
     * @return the locale
     */
    public static Locale resolveAndSetCookie(final HttpServerExchange exchange, final Consumer<Cookie> setCookie) {
        final Cookie requestCookie = exchange.getRequestCookie(I18nCookie.cookieName);
        Locale language;

        if (requestCookie != null) {
            try {
                language = Locale.forLanguageTag(requestCookie.getValue());
                if (!I18nCookie.allowedLocale.contains(language)) {
                    language = null;
                }
            } catch (final Exception ignore) {
                language = null;
            }
        } else {
            language = null;
        }

        if (language == null) {
            language = I18nCookie.localeFromHeader(exchange);
            if (language != null && setCookie != null) {
                setCookie.accept(I18nCookie.createCookie(language));
            }
        }

        if (language == null) {
            language = I18nCookie.defaultLocale;

            if (language != null && setCookie != null) {
                setCookie.accept(I18nCookie.createCookie(language));
            }
        }

        return language;
    }

    /**
     * Resolve and set cookie locale.
     *
     * @param exchange the exchange
     * @return the locale
     */
    public static Locale resolveAndSetCookie(final HttpServerExchange exchange) {
        return I18nCookie.resolveAndSetCookie(exchange, exchange::setResponseCookie);
    }

    /**
     * Locale from cookie locale.
     *
     * @param exchange the exchange
     * @return the locale
     */
    public static Locale resolveLocale(final HttpServerExchange exchange) {
        return I18nCookie.resolveAndSetCookie(exchange, null);
    }

    /**
     * Locale from header locale.
     *
     * @param exchange the exchange
     * @return the locale
     */
    private static Locale localeFromHeader(final HttpServerExchange exchange) {
        final HeaderValues acceptLanguageHeader = exchange.getRequestHeaders().get("Accept-Language");

        if (acceptLanguageHeader != null) {
            final String acceptLanguageHeaderValue = acceptLanguageHeader.getFirst();
            final List<Locale> preferredLocales = I18nCookie.parsePreferredLocales(acceptLanguageHeaderValue);

            // Iterate through the list of preferred locales and return the first match that is explicitly allowed
            for (final Locale preferredLocale : preferredLocales) {
                if (I18nCookie.allowedLocale.contains(preferredLocale)) {
                    return preferredLocale;
                }
            }

            // If no exact match is found, look for matches based solely on language
            for (final Locale preferredLocale : preferredLocales) {
                for (final Locale allowedLocale : I18nCookie.allowedLocale) {
                    if (allowedLocale.getLanguage().equals(preferredLocale.getLanguage())) {
                        return allowedLocale;
                    }
                }
            }
        }

        return null;
    }


    /**
     * Parse locales list.
     *
     * @param acceptLanguageHeader the accept language header
     * @return the list
     */
    public static List<Locale> parsePreferredLocales(final String acceptLanguageHeader) {
        // Split by commas to separate the locales
        final String[] entries = acceptLanguageHeader.split(",");

        // Create a map to store locales and their respective weights
        final Map<String, Double> localeWeights = new HashMap<>();

        for (final String entry : entries) {
            // Split by ";" to separate locale from q-value
            final String[] parts = entry.trim().split(";");

            // The locale is always the first part
            final String locale = parts[0];

            // Determine the weight (q-value)
            double weight = 1.0; // Default weight is 1
            if (parts.length > 1 && parts[1].startsWith("q=")) {
                weight = Double.parseDouble(parts[1].substring(2));
            }

            // Store the locale and weight in the map
            localeWeights.put(locale, weight);
        }

        // Sort the locales by weight in descending order
        return localeWeights.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .map(Locale::forLanguageTag)
                .collect(Collectors.toList());
    }
}
