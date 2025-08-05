package com.merim.digitalpayment.underflow.i18n.tests;

import com.merim.digitalpayment.underflow.i18n.I18n;
import com.merim.digitalpayment.underflow.i18n.sources.MapSource;
import com.merim.digitalpayment.underflow.i18n.sources.PropertiesSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The type 18 n test.
 */
public class I18nTest {

    /**
     * The 18 n.
     */
    private I18n i18n;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        final Properties englishProperties = new Properties();
        englishProperties.setProperty("greeting", "Hello");

        final Properties frenchProperties = new Properties();
        frenchProperties.setProperty("greeting", "Bonjour");

        this.i18n = new I18n()
                .addI18nSource(PropertiesSource.builder()
                        .addLocale(Locale.ENGLISH, englishProperties)
                        .addLocale(Locale.FRENCH, frenchProperties)
                        .build());
    }


    /**
     * Test message retrieval for different locales.
     */
    @Test
    public void testLocalizationMessages() {
        assertEquals("Hello", this.i18n.get(Locale.ENGLISH, "greeting"));
        assertEquals("Bonjour", this.i18n.get(Locale.FRENCH, "greeting"));
        assertEquals("Bonjour", this.i18n.get(new Locale("fr", "MA"), "greeting")); // French / Morocco
    }

    /**
     * Test unsupported locale and key behavior.
     */
    @Test
    public void testUnsupportedLocaleAndKey() {
        assertEquals("key.greeting", this.i18n.get(Locale.GERMAN, "key.greeting"));
        assertEquals("key.farewell", this.i18n.get(Locale.ENGLISH, "key.farewell"));
    }

    /**
     * Test default message handling.
     */
    @Test
    public void testGetOrDefault() {
        assertEquals("Hello", this.i18n.getOrDefault(Locale.ENGLISH, "greeting", "Default"));
        assertEquals("Default", this.i18n.getOrDefault(Locale.ENGLISH, "farewell", "Default"));
    }

    /**
     * Test message formatting with arguments.
     */
    @Test
    public void testMessageFormatting() {
        final Properties englishProperties = new Properties();
        englishProperties.setProperty("greetingWithName", "Hello, {0}!");
        this.i18n.addI18nSource(PropertiesSource.builder()
                .addLocale(Locale.ENGLISH, englishProperties)
                .build());

        assertEquals("Hello, John!", this.i18n.get(Locale.ENGLISH, "greetingWithName", "John"));
    }

    /**
     * Test overriding keys using MapSource.
     */
    @Test
    public void testOverrideKeysWithMapSource() {
        final Map<String, String> overrideMessages = new HashMap<>();
        overrideMessages.put("greeting", "Hi");

        this.i18n.addI18nSource(MapSource.builder()
                .addLocale(Locale.ENGLISH, overrideMessages)
                .build());

        // The first set value is used.
        assertEquals("Hello", this.i18n.get(Locale.ENGLISH, "greeting"));
        assertEquals("Bonjour", this.i18n.get(Locale.FRENCH, "greeting"));
        assertEquals("farewell", this.i18n.get(Locale.ENGLISH, "farewell"));
    }
}
