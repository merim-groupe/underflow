package com.merim.digitalpayment.underflow.i18n.sources;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * AbstractI18nSourceBuilder.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @param <V> the type parameter
 * @author Pierre Adam
 * @since 25.03.10
 */
public class AbstractI18nSourceBuilder<T, U extends AbstractI18nSource<T>, V extends AbstractI18nSourceBuilder<T, U, V>> {

    /**
     * The Locale messages.
     */
    protected final Map<Locale, T> localeMessages;

    /**
     * The Source builder.
     */
    private final Function<Map<Locale, T>, U> sourceBuilder;

    /**
     * Instantiates a new Map source builder.
     *
     * @param sourceBuilder the source builder
     */
    public AbstractI18nSourceBuilder(final Function<Map<Locale, T>, U> sourceBuilder) {
        this.localeMessages = new HashMap<>();
        this.sourceBuilder = sourceBuilder;
    }

    /**
     * Add map map source builder.
     *
     * @param locale   the locale
     * @param messages the messages
     * @return the map source builder
     */
    @SuppressWarnings("unchecked")
    public V addLocale(final Locale locale, @NonNull final T messages) {
        this.localeMessages.put(locale, messages);
        return (V) this;
    }

    /**
     * Build map source.
     *
     * @return the map source
     */
    public U build() {
        return this.sourceBuilder.apply(this.localeMessages);
    }
}
