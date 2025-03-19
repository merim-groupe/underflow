package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.results.http.HtmlResults;
import com.merim.digitalpayment.underflow.results.http.HttpResult;
import freemarker.template.Template;
import io.undertow.io.IoCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HttpString;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FlowDTOWrapperResult.
 *
 * @author Pierre Adam
 * @since 25.03.19
 */
public class DTOWrapperResult implements HttpResult, HtmlResults {

    /**
     * The Template.
     */
    @Getter
    private final Template template;

    /**
     * The Data model.
     */
    @Getter
    private final Object dataModel;

    /**
     * The Io callback.
     */
    private final IoCallback ioCallback;

    /**
     * The Dto wrapper builder.
     */
    private final DTOWrapperBuilder dtoWrapperBuilder;

    /**
     * The Result method.
     */
    private final TemplateResultCallback resultMethod;

    /**
     * The Headers.
     */
    private final Map<HttpString, String> headers;

    /**
     * The Cookies.
     */
    private final List<Cookie> cookies;

    /**
     * The Cookies to delete.
     */
    private final List<String> cookiesToDelete;

    /**
     * The Clear cookies.
     */
    private boolean clearCookies;

    /**
     * Instantiates a new Dto wrapper result.
     *
     * @param template          the template
     * @param dataModel         the data model
     * @param ioCallback        the io callback
     * @param dtoWrapperBuilder the dto wrapper builder
     * @param resultMethod      the result method
     */
    public DTOWrapperResult(final Template template,
                            final Object dataModel,
                            final IoCallback ioCallback,
                            final DTOWrapperBuilder dtoWrapperBuilder,
                            final TemplateResultCallback resultMethod) {
        this.template = template;
        this.dataModel = dataModel;
        this.ioCallback = ioCallback;
        this.dtoWrapperBuilder = dtoWrapperBuilder;
        this.resultMethod = resultMethod;
        this.headers = new HashMap<>();
        this.cookies = new ArrayList<>();
        this.cookiesToDelete = new ArrayList<>();
        this.clearCookies = false;
    }

    /**
     * Instantiates a new Dto wrapper result.
     *
     * @param template          the template
     * @param data              the data
     * @param dtoWrapperBuilder the dto wrapper builder
     * @param resultMethod      the result method
     */
    public DTOWrapperResult(final Template template,
                            final Object data,
                            final DTOWrapperBuilder dtoWrapperBuilder,
                            final TemplateResultCallback resultMethod) {
        this(template, data, IoCallback.END_EXCHANGE, dtoWrapperBuilder, resultMethod);
    }

    @Override
    public void process(final HttpServerExchange exchange, final Method method) {
        final HttpResult result = this.resultMethod
                .forge(this.template,
                        this.dtoWrapperBuilder.build(exchange, this, this.dataModel),
                        this.ioCallback)
                .withCookies(this.cookies)
                .withHeaders(this.headers)
                .deleteCookies(this.cookiesToDelete);

        if (this.clearCookies) {
            result.dropCookies();
        }

        result.process(exchange, method);
    }

    @Override
    public HttpResult withHeader(final HttpString key, final String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public HttpResult withCookie(final Cookie cookie) {
        this.cookies.add(cookie);
        return this;
    }

    @Override
    public HttpResult deleteCookie(final String cookieToDelete) {
        this.cookiesToDelete.add(cookieToDelete);
        return this;
    }

    @Override
    public HttpResult dropCookies() {
        this.clearCookies = true;
        return this;
    }

    /**
     * The interface Template result callback.
     */
    @FunctionalInterface
    public interface TemplateResultCallback {

        /**
         * Forge http result.
         *
         * @param template   the template
         * @param dataModel  the data model
         * @param ioCallback the io callback
         * @return the http result
         */
        HttpResult forge(final Template template, final Object dataModel, final IoCallback ioCallback);
    }
}
