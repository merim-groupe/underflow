package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.results.http.HttpResult;
import com.merim.digitalpayment.underflow.security.FlowSecurity;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.undertow.io.IoCallback;
import io.undertow.util.StatusCodes;

import java.io.File;

/**
 * FlowDTOWrapperTemplateHandler.
 *
 * @author Pierre Adam
 * @since 25.03.19
 */
public class FlowDTOWrapperTemplateHandler extends FlowTemplateHandler {

    /**
     * The Dto wrapper builder.
     */
    private final DTOWrapperBuilder dtoWrapperBuilder;

    /**
     * Instantiates a new Flow dto wrapper handler.
     *
     * @param configuration     the configuration
     * @param dtoWrapperBuilder the dto wrapper builder
     */
    public FlowDTOWrapperTemplateHandler(final Configuration configuration, final DTOWrapperBuilder dtoWrapperBuilder) {
        super(configuration);
        this.dtoWrapperBuilder = dtoWrapperBuilder;
    }

    /**
     * Instantiates a new Flow dto wrapper handler.
     *
     * @param configuration     the configuration
     * @param flowSecurity      the flow security
     * @param dtoWrapperBuilder the dto wrapper builder
     */
    public FlowDTOWrapperTemplateHandler(final Configuration configuration, final FlowSecurity<?, ?> flowSecurity, final DTOWrapperBuilder dtoWrapperBuilder) {
        super(configuration, flowSecurity);
        this.dtoWrapperBuilder = dtoWrapperBuilder;
    }

    /**
     * Instantiates a new Flow dto wrapper handler.
     *
     * @param resourcePath      the resource path
     * @param dtoWrapperBuilder the dto wrapper builder
     */
    public FlowDTOWrapperTemplateHandler(final String resourcePath, final DTOWrapperBuilder dtoWrapperBuilder) {
        super(resourcePath);
        this.dtoWrapperBuilder = dtoWrapperBuilder;
    }

    /**
     * Instantiates a new Flow dto wrapper handler.
     *
     * @param resourcePath      the resource path
     * @param flowSecurity      the flow security
     * @param dtoWrapperBuilder the dto wrapper builder
     */
    public FlowDTOWrapperTemplateHandler(final String resourcePath, final FlowSecurity<?, ?> flowSecurity, final DTOWrapperBuilder dtoWrapperBuilder) {
        super(resourcePath, flowSecurity);
        this.dtoWrapperBuilder = dtoWrapperBuilder;
    }

    /**
     * Instantiates a new Flow dto wrapper handler.
     *
     * @param dirPath           the dir path
     * @param dtoWrapperBuilder the dto wrapper builder
     */
    public FlowDTOWrapperTemplateHandler(final File dirPath, final DTOWrapperBuilder dtoWrapperBuilder) {
        super(dirPath);
        this.dtoWrapperBuilder = dtoWrapperBuilder;
    }

    /**
     * Instantiates a new Flow dto wrapper handler.
     *
     * @param dirPath           the dir path
     * @param flowSecurity      the flow security
     * @param dtoWrapperBuilder the dto wrapper builder
     */
    public FlowDTOWrapperTemplateHandler(final File dirPath, final FlowSecurity<?, ?> flowSecurity, final DTOWrapperBuilder dtoWrapperBuilder) {
        super(dirPath, flowSecurity);
        this.dtoWrapperBuilder = dtoWrapperBuilder;
    }

    @Override
    public HttpResult ok(final Template template, final Object dataModel) {
        return this.wrappedResult(StatusCodes.OK, template, dataModel);
    }

    @Override
    public HttpResult ok(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.wrappedResult(StatusCodes.OK, template, dataModel, ioCallback);
    }

    @Override
    public HttpResult created(final Template template, final Object dataModel) {
        return this.wrappedResult(StatusCodes.CREATED, template, dataModel);
    }

    @Override
    public HttpResult created(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.wrappedResult(StatusCodes.CREATED, template, dataModel, ioCallback);
    }

    @Override
    public HttpResult badRequest(final Template template, final Object dataModel) {
        return this.wrappedResult(StatusCodes.BAD_REQUEST, template, dataModel);
    }

    @Override
    public HttpResult badRequest(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.wrappedResult(StatusCodes.BAD_REQUEST, template, dataModel, ioCallback);
    }

    @Override
    public HttpResult unauthorized(final Template template, final Object dataModel) {
        return this.wrappedResult(StatusCodes.UNAUTHORIZED, template, dataModel);
    }

    @Override
    public HttpResult unauthorized(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.wrappedResult(StatusCodes.UNAUTHORIZED, template, dataModel, ioCallback);
    }

    @Override
    public HttpResult forbidden(final Template template, final Object dataModel) {
        return this.wrappedResult(StatusCodes.FORBIDDEN, template, dataModel);
    }

    @Override
    public HttpResult forbidden(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.wrappedResult(StatusCodes.FORBIDDEN, template, dataModel, ioCallback);
    }

    @Override
    public HttpResult notFound(final Template template, final Object dataModel) {
        return this.wrappedResult(StatusCodes.NOT_FOUND, template, dataModel);
    }

    @Override
    public HttpResult notFound(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.wrappedResult(StatusCodes.NOT_FOUND, template, dataModel, ioCallback);
    }

    @Override
    public HttpResult internalServerError(final Template template, final Object dataModel) {
        return this.wrappedResult(StatusCodes.INTERNAL_SERVER_ERROR, template, dataModel);
    }

    @Override
    public HttpResult internalServerError(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.wrappedResult(StatusCodes.INTERNAL_SERVER_ERROR, template, dataModel, ioCallback);
    }

    @Override
    public HttpResult serviceUnavailable(final Template template, final Object dataModel) {
        return this.wrappedResult(StatusCodes.SERVICE_UNAVAILABLE, template, dataModel);
    }

    @Override
    public HttpResult serviceUnavailable(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.wrappedResult(StatusCodes.SERVICE_UNAVAILABLE, template, dataModel, ioCallback);
    }

    /**
     * Plutus result http result.
     *
     * @param code      the code
     * @param template  the template
     * @param dataModel the data model
     * @return the http result
     */
    public HttpResult wrappedResult(final int code, final Template template, final Object dataModel) {
        return new DTOWrapperResult<>(template, dataModel, this.dtoWrapperBuilder, (t, d, ioc) -> this.result(code, t, d, ioc)) {
        };
    }

    /**
     * Plutus result http result.
     *
     * @param code       the code
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the http result
     */
    public HttpResult wrappedResult(final int code, final Template template, final Object dataModel, final IoCallback ioCallback) {
        return new DTOWrapperResult<>(template, dataModel, ioCallback, this.dtoWrapperBuilder, (t, d, ioc) -> this.result(code, t, d, ioc));
    }
}