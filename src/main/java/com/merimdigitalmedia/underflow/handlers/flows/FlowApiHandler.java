package com.merimdigitalmedia.underflow.handlers.flows;

import com.merimdigitalmedia.underflow.api.ApiForm;
import com.merimdigitalmedia.underflow.api.ApiFormWithPayload;
import com.merimdigitalmedia.underflow.api.ServerError;
import com.merimdigitalmedia.underflow.results.Result;
import com.merimdigitalmedia.underflow.results.http.JsonResults;
import com.merimdigitalmedia.underflow.security.FlowSecurity;
import com.merimdigitalmedia.underflow.utils.Application;
import com.merimdigitalmedia.underflow.utils.SmartGZipBodyInput;
import io.undertow.server.HttpServerExchange;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

/**
 * FlowApiHandler.
 *
 * @author Pierre Adam
 * @since 22.07.20
 */
public class FlowApiHandler extends FlowHandler implements JsonResults {

    /**
     * Instantiates a new Flow api handler.
     */
    public FlowApiHandler() {
        super();
    }

    /**
     * Instantiates a new Flow api handler.
     *
     * @param flowSecurity the flow security
     */
    public FlowApiHandler(final FlowSecurity<?, ?> flowSecurity) {
        super(flowSecurity);
    }

    /**
     * Gets json body.
     *
     * @param <T>      the type parameter
     * @param exchange the exchange
     * @param tClass   the t class
     * @param logic    the logic
     * @return the json body
     */
    protected <T> Result getJsonBody(final HttpServerExchange exchange,
                                     final Class<T> tClass,
                                     final Function<T, Result> logic) {
        final SmartGZipBodyInput bodyInput = new SmartGZipBodyInput(exchange);
        try (final InputStream inputStream = bodyInput.getInputStream()) {
            final T t = Application.getMapper().readerFor(tClass).readValue(inputStream);
            return logic.apply(t);
        } catch (final IOException e) {
            this.logger.debug("Invalid input body.", e);
            final ServerError serverError = new ServerError("Bad request",
                    "The request body was not formatted as expected or is unreadable.",
                    e.getMessage());
            return this.badRequest(this.toJsonNode(serverError));
        }
    }

    /**
     * Gets json form.
     *
     * @param <T>      the type parameter
     * @param exchange the exchange
     * @param tClass   the t class
     * @param logic    the logic
     * @return the json form
     */
    protected <T extends ApiForm> Result getJsonForm(
            final HttpServerExchange exchange,
            final Class<T> tClass,
            final Function<T, Result> logic) {
        return this.getJsonBody(exchange, tClass, form -> {
            final Optional<ServerError> optionalError = form.isValid();
            if (optionalError.isPresent()) {
                final ServerError error = optionalError.get();
                return this.badRequest(this.toJsonNode(new ServerError("Bad request", error.getMessage(), error.getCause())));
            } else {
                return logic.apply(form);
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
     * @param logic    the logic
     * @return the json form with payload
     */
    protected <T extends ApiFormWithPayload<U>, U> Result getJsonFormWithPayload(
            final HttpServerExchange exchange,
            final Class<T> tClass,
            final U payload,
            final Function<T, Result> logic) {
        return this.getJsonBody(exchange, tClass, form -> {
            final Optional<ServerError> optionalError = form.isValid(payload);
            if (optionalError.isPresent()) {
                final ServerError error = optionalError.get();
                return this.badRequest(this.toJsonNode(new ServerError("Bad request", error.getMessage(), error.getCause())));
            } else {
                return logic.apply(form);
            }
        });
    }

    @Override
    public Result onNotFound() {
        return this.notFound(this.toJsonNode(new ServerError("Not Found", "This request has been logged.")));
    }

    @Override
    public Result onUnauthorized() {
        return this.unauthorized(this.toJsonNode(new ServerError("Unauthorized", "This request has been logged.")));
    }

    @Override
    public Result onForbidden() {
        return this.forbidden(this.toJsonNode(new ServerError("Forbidden", "This request has been logged.")));
    }

    @Override
    public Result onException(final Throwable exception) {
        return this.internalServerError(this.toJsonNode(new ServerError("Internal Server Error", exception)));
    }
}
