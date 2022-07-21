package com.merimdigitalmedia.underflow.forms;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

/**
 * FormService.
 *
 * @author Pierre Adam
 * @since 21.10.05
 */
public interface WebForm {

    /**
     * Gets form data.
     *
     * @param exchange the exchange
     * @return the form data
     * @throws IOException the io exception
     */
    default FormData getFormData(final HttpServerExchange exchange) throws IOException {
        final FormDataParser p = FormParserFactory.builder(true).build().createParser(exchange);
        if (p == null) {
            return null;
        }
        p.setCharacterEncoding("UTF-8");
        return p.parseBlocking();
    }

    /**
     * Gets form.
     *
     * @param <T>          the type parameter
     * @param exchange     the exchange
     * @param tClass       the t class
     * @param successLogic the success logic
     * @param errorLogic   the error logic
     */
    default <T extends Form> void getForm(final HttpServerExchange exchange,
                                          final Class<T> tClass,
                                          final Consumer<T> successLogic,
                                          final Consumer<Exception> errorLogic) {
        final Logger logger = LoggerFactory.getLogger(WebForm.class);
        try {
            final FormData formData = this.getFormData(exchange);
            final T instance = tClass.getDeclaredConstructor().newInstance();
            try {
                instance.accept(exchange, formData);
            } catch (final Exception e) {
                errorLogic.accept(e);
                return;
            }
            successLogic.accept(instance);
        } catch (final InvocationTargetException | InstantiationException | IllegalAccessException e) {
            logger.error("Unable to instantiate a form of {}.", tClass.getCanonicalName(), e);
        } catch (final IOException e) {
            logger.error("Unable to get the form data.", e);
        } catch (final NoSuchMethodException e) {
            logger.error("The class {} does not have a default constructor.", tClass.getCanonicalName(), e);
        }
    }
}
