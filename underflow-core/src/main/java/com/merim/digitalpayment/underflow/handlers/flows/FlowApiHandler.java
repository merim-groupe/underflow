package com.merim.digitalpayment.underflow.handlers.flows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.merim.digitalpayment.underflow.api.forms.ApiForm;
import com.merim.digitalpayment.underflow.api.forms.ApiFormWithPayload;
import com.merim.digitalpayment.underflow.api.forms.FormError;
import com.merim.digitalpayment.underflow.entities.ServerError;
import com.merim.digitalpayment.underflow.entities.ServerFormError;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.results.http.JsonResults;
import com.merim.digitalpayment.underflow.security.FlowSecurity;
import com.merim.digitalpayment.underflow.utils.Application;
import com.merim.digitalpayment.underflow.utils.SmartGZipBodyInput;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
     * @param <T>             the type parameter
     * @param mapper          the mapper
     * @param bodyInputStream the body input stream
     * @param tClass          the t class
     * @param logic           the logic
     * @return the json body
     */
    protected <T> Result getJsonBody(
            final ObjectMapper mapper,
            final InputStream bodyInputStream,
            final Class<T> tClass,
            final Function<T, Result> logic) {
        final SmartGZipBodyInput bodyInput = new SmartGZipBodyInput(bodyInputStream);
        try (final InputStream inputStream = bodyInput.getInputStream()) {
            final T t = mapper.readerFor(tClass).readValue(inputStream);
            mapper.readerFor(tClass).readValue(inputStream);
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
     * Gets json body.
     *
     * @param <T>             the type parameter
     * @param bodyInputStream the body input stream
     * @param tClass          the t class
     * @param logic           the logic
     * @return the json body
     */
    protected <T> Result getJsonBody(
            final InputStream bodyInputStream,
            final Class<T> tClass,
            final Function<T, Result> logic) {
        return this.getJsonBody(Application.getMapper(), bodyInputStream, tClass, logic);
    }

    /**
     * Gets json form.
     *
     * @param <T>             the type parameter
     * @param mapper          the mapper
     * @param bodyInputStream the body input stream
     * @param tClass          the t class
     * @param logic           the logic
     * @return the json form
     */
    protected <T extends ApiForm> Result getJsonForm(
            final ObjectMapper mapper,
            final InputStream bodyInputStream,
            final Class<T> tClass,
            final Function<T, Result> logic) {
        return this.getJsonBody(mapper, bodyInputStream, tClass, form -> {
            final List<FormError> errors = form.isValid();
            if (errors != null && errors.size() > 0) {
                return this.badRequest(this.toJsonNode(new ServerFormError(errors)));
            } else {
                return logic.apply(form);
            }
        });
    }

    /**
     * Gets json form.
     *
     * @param <T>             the type parameter
     * @param bodyInputStream the body input stream
     * @param tClass          the t class
     * @param logic           the logic
     * @return the json form
     */
    protected <T extends ApiForm> Result getJsonForm(
            final InputStream bodyInputStream,
            final Class<T> tClass,
            final Function<T, Result> logic) {
        return this.getJsonForm(Application.getMapper(), bodyInputStream, tClass, logic);
    }

    /**
     * Gets json form.
     *
     * @param <T>             the type parameter
     * @param <U>             the type parameter
     * @param mapper          the mapper
     * @param bodyInputStream the body input stream
     * @param tClass          the t class
     * @param payload         the payload
     * @param logic           the logic
     * @return the json form with payload
     */
    protected <T extends ApiFormWithPayload<U>, U> Result getJsonFormWithPayload(
            final ObjectMapper mapper,
            final InputStream bodyInputStream,
            final Class<T> tClass,
            final U payload,
            final Function<T, Result> logic) {
        return this.getJsonBody(mapper, bodyInputStream, tClass, form -> {
            final List<FormError> errors = form.isValid(payload);
            if (errors != null && errors.size() > 0) {
                return this.badRequest(this.toJsonNode(new ServerFormError(errors)));
            } else {
                return logic.apply(form);
            }
        });
    }

    /**
     * Gets json form.
     *
     * @param <T>             the type parameter
     * @param <U>             the type parameter
     * @param bodyInputStream the body input stream
     * @param tClass          the t class
     * @param payload         the payload
     * @param logic           the logic
     * @return the json form with payload
     */
    protected <T extends ApiFormWithPayload<U>, U> Result getJsonFormWithPayload(
            final InputStream bodyInputStream,
            final Class<T> tClass,
            final U payload,
            final Function<T, Result> logic) {
        return this.getJsonFormWithPayload(Application.getMapper(), bodyInputStream, tClass, payload, logic);
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
