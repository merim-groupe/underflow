package com.merimdigitalmedia.underflow.handlers.flows.answers;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.function.Consumer;

/**
 * FlowAnswer.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public interface FlowHtmlAnswer {

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange  the exchange
     * @param template  the template
     * @param dataModel the data model
     */
    default void ok(final HttpServerExchange exchange, final Template template, final Object dataModel) {
        this.result(exchange, 200, template, dataModel);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange   the exchange
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     */
    default void ok(final HttpServerExchange exchange, final Template template, final Object dataModel, final IoCallback ioCallback) {
        this.result(exchange, 200, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange  the exchange
     * @param template  the template
     * @param dataModel the data model
     */
    default void created(final HttpServerExchange exchange, final Template template, final Object dataModel) {
        this.result(exchange, 201, template, dataModel);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange   the exchange
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     */
    default void created(final HttpServerExchange exchange, final Template template, final Object dataModel, final IoCallback ioCallback) {
        this.result(exchange, 201, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange  the exchange
     * @param template  the template
     * @param dataModel the data model
     */
    default void badRequest(final HttpServerExchange exchange, final Template template, final Object dataModel) {
        this.result(exchange, 400, template, dataModel);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange   the exchange
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     */
    default void badRequest(final HttpServerExchange exchange, final Template template, final Object dataModel, final IoCallback ioCallback) {
        this.result(exchange, 400, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange  the exchange
     * @param template  the template
     * @param dataModel the data model
     */
    default void forbidden(final HttpServerExchange exchange, final Template template, final Object dataModel) {
        this.result(exchange, 403, template, dataModel);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange   the exchange
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     */
    default void forbidden(final HttpServerExchange exchange, final Template template, final Object dataModel, final IoCallback ioCallback) {
        this.result(exchange, 403, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange  the exchange
     * @param template  the template
     * @param dataModel the data model
     */
    default void notFound(final HttpServerExchange exchange, final Template template, final Object dataModel) {
        this.result(exchange, 404, template, dataModel);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     */
    default void notFound(final HttpServerExchange exchange, final Template template, final Object dataModel, final IoCallback ioCallback) {
        this.result(exchange, 404, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange  the exchange
     * @param template  the template
     * @param dataModel the data model
     */
    default void internalServerError(final HttpServerExchange exchange, final Template template, final Object dataModel) {
        this.result(exchange, 500, template, dataModel);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange  the exchange
     * @param template  the template
     * @param dataModel the data model
     */
    default void internalServerError(final HttpServerExchange exchange, final Template template, final Object dataModel, final IoCallback ioCallback) {
        this.result(exchange, 500, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange  the exchange
     * @param template  the template
     * @param dataModel the data model
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final Template template, final Object dataModel) {
        this.result(exchange, 503, template, dataModel);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange   the exchange
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     */
    default void serviceUnavailable(final HttpServerExchange exchange, final Template template, final Object dataModel, final IoCallback ioCallback) {
        this.result(exchange, 503, template, dataModel, ioCallback);
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange  the exchange
     * @param code      the code
     * @param template  the template
     * @param dataModel the data model
     */
    default void result(final HttpServerExchange exchange, final int code, final Template template, final Object dataModel) {
        this.result(exchange, code, template, dataModel, IoCallback.END_EXCHANGE);
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange   the exchange
     * @param code       the code
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     */
    default void result(final HttpServerExchange exchange, final int code, final Template template, final Object dataModel, final IoCallback ioCallback) {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final Writer writer = new OutputStreamWriter(output);

        try {
            template.process(dataModel, writer);
        } catch (final TemplateException | IOException e) {
            LoggerFactory.getLogger(this.getClass()).error("Something bad happened while rendering the template. !", e);
        }
        this.result(exchange, code, sender -> sender.send(output.toString(), ioCallback));
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange     the exchange
     * @param code         the code
     * @param exchangeData the exchange data
     */
    void result(final HttpServerExchange exchange, final int code, final Consumer<Sender> exchangeData);
}
