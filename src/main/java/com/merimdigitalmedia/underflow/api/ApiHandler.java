package com.merimdigitalmedia.underflow.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.merimdigitalmedia.underflow.FlowHandler;
import com.merimdigitalmedia.underflow.api.entities.ApiForm;
import com.merimdigitalmedia.underflow.api.entities.ApiFormWithPayload;
import com.merimdigitalmedia.underflow.api.entities.ServerError;
import com.merimdigitalmedia.underflow.utils.SmartGZipBodyInput;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * ApiHandler.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
public class ApiHandler extends FlowHandler {

    /**
     * The Object mapper.
     */
    final protected ObjectMapper objectMapper;

    /**
     * Instantiates a new Base handler.
     */
    public ApiHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * Gets json body.
     *
     * @param <T>      the type parameter
     * @param exchange the exchange
     * @param tClass   the t class
     * @param consumer the consumer
     */
    protected <T> void getJsonBody(final HttpServerExchange exchange,
                                   final Class<T> tClass,
                                   final Consumer<T> consumer) {
        final SmartGZipBodyInput bodyInput = new SmartGZipBodyInput(exchange);
        try (final InputStream inputStream = bodyInput.getInputStream()) {
            final T t = this.objectMapper.readerFor(tClass).readValue(inputStream);
            consumer.accept(t);
        } catch (final IOException e) {
            this.logger.debug("Invalid input body.", e);
            this.badRequest(exchange, sender -> sender.send(this.getServerErrorAsString(
                    new ServerError("Bad request",
                            "The request body was not formatted as expected or is unreadable.",
                            e.getMessage())
            )));
        }
    }

    /**
     * Gets json form.
     *
     * @param <T>      the type parameter
     * @param exchange the exchange
     * @param tClass   the t class
     * @param consumer the consumer
     */
    protected <T extends ApiForm> void getJsonForm(
            final HttpServerExchange exchange,
            final Class<T> tClass,
            final Consumer<T> consumer) {
        this.getJsonBody(exchange, tClass, form -> {
            final ServerError error = form.isValid();
            if (error != null) {
                this.badRequest(exchange, sender -> sender.send(this.getServerErrorAsString(
                        new ServerError("Bad request", error.getMessage(), error.getCause())
                )));
            } else {
                consumer.accept(form);
            }
        });
    }

    /**
     * Gets json form.
     *
     * @param <T>      the type parameter
     * @param <U>      the type parameter
     * @param exchange the exchange
     * @param tClass   the t class
     * @param payload  the payload
     * @param consumer the consumer
     */
    protected <T extends ApiFormWithPayload<U>, U> void getJsonFormWithPayload(
            final HttpServerExchange exchange,
            final Class<T> tClass,
            final U payload,
            final Consumer<T> consumer) {
        this.getJsonBody(exchange, tClass, form -> {
            final ServerError error = form.isValid(payload);
            if (error != null) {
                this.badRequest(exchange, sender -> sender.send(this.getServerErrorAsString(
                        new ServerError("Bad request", error.getMessage(), error.getCause())
                )));
            } else {
                consumer.accept(form);
            }
        });
    }

    /**
     * Gets as json.
     *
     * @param exchange     the exchange
     * @param objClass     the obj class
     * @param obj          the obj
     * @param jsonConsumer the json consumer
     */
    protected void prepareJsonResult(final HttpServerExchange exchange, final Class<?> objClass, final Object obj, final Consumer<String> jsonConsumer) {
        try {
            jsonConsumer.accept(this.objectMapper.writerFor(objClass).writeValueAsString(obj));
        } catch (final JsonProcessingException e) {
            this.handleJsonProcessingException(exchange, e);
        }
    }

    /**
     * Prepare json result.
     *
     * @param exchange     the exchange
     * @param obj          the obj
     * @param jsonConsumer the json consumer
     */
    protected void prepareJsonResult(final HttpServerExchange exchange, final Object obj, final Consumer<String> jsonConsumer) {
        try {
            exchange.getResponseHeaders().put(new HttpString("Content-Type"), "application/json");
            jsonConsumer.accept(this.objectMapper.writeValueAsString(obj));
        } catch (final JsonProcessingException e) {
            this.handleJsonProcessingException(exchange, e);
        }
    }

    /**
     * Handle json processing exception.
     *
     * @param exchange the exchange
     * @param e        the e
     */
    private void handleJsonProcessingException(final HttpServerExchange exchange, final Exception e) {
        this.logger.error("An error occurred.", e);
        this.internalServerError(exchange, sender -> sender.send(this.getServerErrorAsString(
                new ServerError("Internal Server Error",
                        "Something went wrong while creating the JSON response.",
                        e.getMessage()))
        ));
    }

    /**
     * Gets server error.
     *
     * @param serverError the server error
     * @return the server error
     */
    protected String getServerErrorAsString(final ServerError serverError) {
        try {
            return this.objectMapper.writerFor(ServerError.class).writeValueAsString(serverError);
        } catch (final JsonProcessingException ignore) {
            return "Internal Server Error";
        }
    }
}
