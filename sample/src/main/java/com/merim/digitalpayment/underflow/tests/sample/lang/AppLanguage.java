package com.merim.digitalpayment.underflow.tests.sample.lang;

import com.merim.digitalpayment.underflow.converters.IConverter;
import lombok.Getter;

import java.util.Locale;

/**
 * AppLanguage.
 *
 * @author Pierre Adam
 * @since 25.03.10
 */
@Getter
public enum AppLanguage {

    /**
     * French app language.
     */
    FRENCH(Locale.FRENCH),

    /**
     * English app language.
     */
    ENGLISH(Locale.ENGLISH);

    /**
     * The Locale.
     */
    private final Locale locale;

    /**
     * Instantiates a new App language.
     *
     * @param locale the locale
     */
    AppLanguage(final Locale locale) {
        this.locale = locale;
    }

    /**
     * The type Converter.
     */
    public static class Converter implements IConverter<AppLanguage> {

        @Override
        public AppLanguage bind(final String representation) {
            if (representation == null) {
                return AppLanguage.ENGLISH;
            }

            try {
                return AppLanguage.valueOf(representation.toUpperCase());
            } catch (final IllegalArgumentException e) {
                return AppLanguage.ENGLISH;
            }
        }

        @Override
        public String unbind(final AppLanguage object) {
            return object.name();
        }

        @Override
        public Class<AppLanguage> getBackedType() {
            return AppLanguage.class;
        }
    }
}
