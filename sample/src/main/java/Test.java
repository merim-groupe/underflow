import com.merim.digitalpayment.underflow.i18n.messageformat.AdvancedMessageFormat;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;

/**
 * Test.
 *
 * @author Pierre Adam
 * @since 25.08.04
 */
public class Test {

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(final String[] args) {
        Locale.setDefault(Locale.FRANCE);
        final MessageFormat messageFormat = new MessageFormat("Hello {0} {1,number,currency}");

        final Object[] objects = new Object[2];
        objects[0] = "John";
        objects[1] = 123.50;

        final String formatted = messageFormat.format(objects);//MessageFormat.format("Hello {0} {1,number,currency}", "John", 123.50);


        final String formatted2 = AdvancedMessageFormat.format("Hello {name}, the value is {value,number,currency}", new HashMap<String, Object>() {{
            this.put("name", "John");
            this.put("value", 123.50);
        }});

        System.out.println(formatted2);
    }

    /**
     * The type Message format dict.
     */
    public static class MessageFormatDict {
        /**
         * The Message.
         */
        private String message;

        /**
         * Instantiates a new Message format dict.
         *
         * @param message the message
         */
        private MessageFormatDict(final String message) {
            this.message = message;
        }

        /**
         * Format string.
         *
         * @param message the message
         * @param dict    the dict
         * @return the string
         */
        public static String format(final String message, final HashMap<String, String> dict) {
            final MessageFormatDict formatter = new MessageFormatDict(message);
            return formatter.format(dict);
        }

        /**
         * Format string.
         *
         * @param dict the dict
         * @return the string
         */
        private String format(final HashMap<String, String> dict) {
            dict.forEach((key, value) -> this.message = this.message.replaceAll("\\{" + key + "\\}", value));

            return this.message;
        }
    }
}
