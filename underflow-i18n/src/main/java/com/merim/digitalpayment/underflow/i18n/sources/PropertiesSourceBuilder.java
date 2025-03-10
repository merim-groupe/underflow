package com.merim.digitalpayment.underflow.i18n.sources;

import lombok.NonNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * PropertiesSourceBuilder.
 *
 * @author Pierre Adam
 * @since 25.03.10
 */
public class PropertiesSourceBuilder {

    /**
     * The Locale messages.
     */
    private final Map<Locale, Properties> localeMessages;

    /**
     * Instantiates a new Properties source builder.
     */
    PropertiesSourceBuilder() {
        this.localeMessages = new HashMap<>();
    }

    /**
     * Add properties properties source builder.
     *
     * @param locale     the locale
     * @param properties the properties
     * @return the properties source builder
     */
    public PropertiesSourceBuilder addProperties(final Locale locale, @NonNull final Properties properties) {
        this.localeMessages.put(locale, properties);
        return this;
    }

    public PropertiesSource build() {
        return new PropertiesSource(this.localeMessages);
    }
}
