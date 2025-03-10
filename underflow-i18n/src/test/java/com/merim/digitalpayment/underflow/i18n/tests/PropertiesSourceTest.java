package com.merim.digitalpayment.underflow.i18n.tests;

import com.merim.digitalpayment.underflow.i18n.sources.PropertiesSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        this.propertiesSource = PropertiesSource.builder()
                .addLocale(Locale.ENGLISH, properties)
                .build();
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
