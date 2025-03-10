package com.merim.digitalpayment.underflow.i18n.tests;

import com.merim.digitalpayment.underflow.i18n.sources.PropertiesSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PropertiesSourceTest
 * Test specific scenario for PropertiesSource.
 *
 * @author Pierre Adam
 */
public class PropertiesSourceTest {

    /**
     * The Properties source.
     */
    private PropertiesSource propertiesSource;

    /**
     * Sets up.
     */
    @BeforeEach
    public void setUp() {
        final Properties properties = new Properties();
        properties.put("key1", "value1");
        this.propertiesSource = new PropertiesSource();
        this.propertiesSource.addProperties(Locale.ENGLISH, properties);
    }


    /**
     * Test add properties overwriting existing key.
     */
    @Test
    public void testAddPropertiesOverwritingExistingKey() {
        final Properties updatedProperties = new Properties();
        updatedProperties.put("key1", "newValue");
        this.propertiesSource.addProperties(Locale.ENGLISH, updatedProperties);

        final String value = this.propertiesSource.getMessage(Locale.ENGLISH, "key1").orElse(null);
        assertEquals("newValue", value);
    }

    /**
     * Test add properties for different locales.
     */
    @Test
    public void testAddPropertiesForDifferentLocales() {
        final Properties frenchProperties = new Properties();
        frenchProperties.put("key1", "valeur1");
        this.propertiesSource.addProperties(Locale.FRENCH, frenchProperties);

        final String valueEnglish = this.propertiesSource.getMessage(Locale.ENGLISH, "key1").orElse(null);
        final String valueFrench = this.propertiesSource.getMessage(Locale.FRENCH, "key1").orElse(null);

        assertEquals("value1", valueEnglish);
        assertEquals("valeur1", valueFrench);
    }

    /**
     * Test has locale.
     */
    @Test
    public void testHasLocale() {
        final boolean hasEnglish = this.propertiesSource.hasLocale(Locale.ENGLISH);
        final boolean hasFrench = this.propertiesSource.hasLocale(Locale.FRENCH);

        assertTrue(hasEnglish);
        assertFalse(hasFrench);
    }

    /**
     * Test has message for key only.
     */
    @Test
    public void testHasMessageForKeyOnly() {
        final boolean hasKey = this.propertiesSource.hasMessage("key1");
        final boolean missingKey = this.propertiesSource.hasMessage("missingKey");

        assertTrue(hasKey);
        assertFalse(missingKey);
    }

    /**
     * Test has message for locale and key.
     */
    @Test
    public void testHasMessageForLocaleAndKey() {
        final boolean hasKeyEnglish = this.propertiesSource.hasMessage(Locale.ENGLISH, "key1");
        final boolean missingKeyEnglish = this.propertiesSource.hasMessage(Locale.ENGLISH, "missingKey");
        final boolean missingKeyFrench = this.propertiesSource.hasMessage(Locale.FRENCH, "key1");

        assertTrue(hasKeyEnglish);
        assertFalse(missingKeyEnglish);
        assertFalse(missingKeyFrench);
    }
}
