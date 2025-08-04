package com.merim.digitalpayment.underflow.i18n.tests;

import com.merim.digitalpayment.underflow.i18n.messageformat.AdvancedMessageFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * AdvancedMessageFormatTest.
 *
 * @author Pierre Adam
 * @since 25.08.04
 */
public class AdvancedMessageFormatTest {

    @Test
    public void testMessageFormatCompatibility() {
        final String message1 = MessageFormat.format("{0} {1} {2}", "Hello", "World");
        final String message2 = AdvancedMessageFormat.format("{0} {1} {2}", "Hello", "World");

        Assertions.assertEquals(message1, message2);
    }

    @Test
    public void testNewFormat() {
        final String message1 = MessageFormat.format("{0} {1}", "Hello", "World");
        final String message2 = AdvancedMessageFormat.format("{greeting} {target}", new HashMap<String, Object>() {{
            this.put("greeting", "Hello");
            this.put("target", "World");
        }});

        Assertions.assertEquals(message1, message2);
    }

    @Test
    public void testFormattingOption() {
        final String message1 = AdvancedMessageFormat.format("{0} {1}", "Hello", "World");
        final String message2 = AdvancedMessageFormat.format("{greeting} {target}", new HashMap<String, Object>() {{
            this.put("greeting", "Hello");
            this.put("target", "World");
        }});

        Assertions.assertEquals(message1, message2);
    }

    @Test
    public void testResult1() {
        final String message = AdvancedMessageFormat.format("{greeting} {target}", new HashMap<String, Object>() {{
            this.put("greeting", "Hello");
            this.put("target", "World");
        }});

        Assertions.assertEquals("Hello World", message);
    }

    @Test
    public void testResult2() {
        final String message = AdvancedMessageFormat.format("{greeting} {target} {unknown}", new HashMap<String, Object>() {{
            this.put("greeting", "Hello");
            this.put("target", "World");
        }});

        Assertions.assertEquals("Hello World {unknown}", message);
    }

    @Test
    public void testResult3() {
        final String message = AdvancedMessageFormat.format("'{'ignore'}' {greeting} {target} {unknown}", new HashMap<String, Object>() {{
            this.put("greeting", "Hello");
            this.put("target", "World");
        }});

        Assertions.assertEquals("{ignore} Hello World {unknown}", message);
    }
}
