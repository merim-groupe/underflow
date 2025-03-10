package com.merim.digitalpayment.underflow.i18n.sources;

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
     * Instantiates a new Map source.
     *
     * @param localeMessages the locale messages
     */
    MapSource(final Map<Locale, Map<String, String>> localeMessages) {
        super(localeMessages);
    }

    /**
     * Builder map source builder.
     *
     * @return the map source builder
     */
    public static MapSourceBuilder builder() {
        return new MapSourceBuilder();
    }

    @Override
    public Optional<String> getMessage(final Locale locale, final String key) {
        if (key == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.localeMessages.getOrDefault(locale, Collections.emptyMap()).get(key));
    }
}
