package com.merim.digitalpayment.underflow.i18n.tests;

import com.merim.digitalpayment.underflow.i18n.I18nSource;
import com.merim.digitalpayment.underflow.i18n.sources.MapSource;
import com.merim.digitalpayment.underflow.i18n.sources.PropertiesSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * I18nSourceTest.
 * Tests for I18nSource with PropertiesSource and MapSource implementations.
 *
 * @author Pierre Adam
 */
public class I18nSourceTest {

    /**
     * The Properties source.
     */
    private I18nSource propertiesSource;

    /**
     * The Map source.
     */
    private I18nSource mapSource;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        // Initialize PropertiesSource
        final Properties propertiesEnglish = new Properties();
        propertiesEnglish.put("greeting", "Hello");
        propertiesEnglish.put("farewell", "Goodbye");

        final Properties propertiesFrench = new Properties();
        propertiesFrench.put("greeting", "Bonjour");
        propertiesFrench.put("farewell", "Au revoir");

        this.propertiesSource = new PropertiesSource()
                .addProperties(Locale.ENGLISH, propertiesEnglish)
                .addProperties(Locale.FRENCH, propertiesFrench);

        // Initialize MapSource
        final Map<String, String> englishMap = new HashMap<>();
        englishMap.put("greeting", "Hello");
        englishMap.put("farewell", "Goodbye");

        final Map<String, String> frenchMap = new HashMap<>();
        frenchMap.put("greeting", "Bonjour");
        frenchMap.put("farewell", "Au revoir");

        this.mapSource = new MapSource()
                .addMap(Locale.ENGLISH, englishMap)
                .addMap(Locale.FRENCH, frenchMap);
    }

    /**
     * Test get available locales.
     */
    @Test
    public void testGetAvailableLocales() {
        assertTrue(this.propertiesSource.getAvailableLocales().contains(Locale.ENGLISH));
        assertTrue(this.propertiesSource.getAvailableLocales().contains(Locale.FRENCH));
        assertFalse(this.propertiesSource.getAvailableLocales().contains(Locale.GERMAN));

        assertTrue(this.mapSource.getAvailableLocales().contains(Locale.ENGLISH));
        assertTrue(this.mapSource.getAvailableLocales().contains(Locale.FRENCH));
        assertFalse(this.mapSource.getAvailableLocales().contains(Locale.GERMAN));
    }

    /**
     * Test has locale.
     */
    @Test
    public void testHasLocale() {
        assertTrue(this.propertiesSource.hasLocale(Locale.ENGLISH));
        assertTrue(this.propertiesSource.hasLocale(Locale.FRENCH));
        assertFalse(this.propertiesSource.hasLocale(Locale.GERMAN));

        assertTrue(this.mapSource.hasLocale(Locale.ENGLISH));
        assertTrue(this.mapSource.hasLocale(Locale.FRENCH));
        assertFalse(this.mapSource.hasLocale(Locale.GERMAN));
    }

    /**
     * Test has message by key.
     */
    @Test
    public void testHasMessageByKey() {
        assertTrue(this.propertiesSource.hasMessage("greeting"));
        assertTrue(this.propertiesSource.hasMessage("farewell"));
        assertFalse(this.propertiesSource.hasMessage("unknown"));

        assertTrue(this.mapSource.hasMessage("greeting"));
        assertTrue(this.mapSource.hasMessage("farewell"));
        assertFalse(this.mapSource.hasMessage("unknown"));
    }

    /**
     * Test has message for locale and key.
     */
    @Test
    public void testHasMessageForLocaleAndKey() {
        assertTrue(this.propertiesSource.hasMessage(Locale.ENGLISH, "greeting"));
        assertTrue(this.propertiesSource.hasMessage(Locale.FRENCH, "farewell"));
        assertFalse(this.propertiesSource.hasMessage(Locale.ENGLISH, "unknown"));
        assertFalse(this.propertiesSource.hasMessage(Locale.GERMAN, "greeting"));

        assertTrue(this.mapSource.hasMessage(Locale.ENGLISH, "greeting"));
        assertTrue(this.mapSource.hasMessage(Locale.FRENCH, "farewell"));
        assertFalse(this.mapSource.hasMessage(Locale.ENGLISH, "unknown"));
        assertFalse(this.mapSource.hasMessage(Locale.GERMAN, "greeting"));
    }

    /**
     * Test get message.
     */
    @Test
    public void testGetMessage() {
        assertEquals("Hello", this.propertiesSource.getMessage(Locale.ENGLISH, "greeting").orElse(null));
        assertEquals("Bonjour", this.propertiesSource.getMessage(Locale.FRENCH, "greeting").orElse(null));
        assertNull(this.propertiesSource.getMessage(Locale.ENGLISH, "unknown").orElse(null));

        assertEquals("Hello", this.mapSource.getMessage(Locale.ENGLISH, "greeting").orElse(null));
        assertEquals("Bonjour", this.mapSource.getMessage(Locale.FRENCH, "greeting").orElse(null));
        assertNull(this.mapSource.getMessage(Locale.ENGLISH, "unknown").orElse(null));
    }
}
