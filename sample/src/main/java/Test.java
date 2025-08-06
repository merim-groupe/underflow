import com.merim.digitalpayment.underflow.i18n.messageformat.AdvancedMessageFormat;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.Currency;
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

        final Locale locale = new Locale("fr", "MA");

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        symbols.setCurrencySymbol("dh");

        DecimalFormat currencyFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
        currencyFormat.setDecimalFormatSymbols(symbols);

        MessageFormat messageFormat = new MessageFormat("Hello {0} {1,number,currency}", locale);
        messageFormat.setFormat(1, currencyFormat);

        Object[] objects = {"John", 123.50};
        System.out.println(messageFormat.format(objects));

        //System.out.println(formatted2);
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
