package com.merim.digitalpayment.underflow.i18n.sources;

import lombok.NonNull;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * MapSource.
 *
 * @author Pierre Adam
 * @since 25.03.05
 */
public class MapSource extends AbstractI18nSource<Map<String, String>> {

    /**
     * Instantiates a new In memory source.
     */
    public MapSource() {
        super();
    }

    /**
     * Add locale.
     *
     * @param locale   the locale
     * @param messages the messages
     * @return the map source
     */
    public MapSource addMap(final Locale locale, @NonNull final Map<String, String> messages) {
        this.localeMessages.put(locale, messages);
        return this;
    }

    @Override
    public Optional<String> getMessage(final Locale locale, final String key) {
        if (key == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.localeMessages.getOrDefault(locale, Collections.emptyMap()).get(key));
    }
}
