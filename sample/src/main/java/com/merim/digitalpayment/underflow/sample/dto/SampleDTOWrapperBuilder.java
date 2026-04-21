package com.merim.digitalpayment.underflow.sample.dto;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.handlers.flows.DTOWrapperBuilder;
import com.merim.digitalpayment.underflow.i18n.I18n;
import com.merim.digitalpayment.underflow.i18n.cookie.I18nCookie;
import com.merim.digitalpayment.underflow.results.http.HttpResult;
import io.undertow.server.HttpServerExchange;

import java.util.Locale;

/**
 * SampleDTOWrapper.
 *
 * @author Pierre Adam
 * @since 25.03.19
 */
public class SampleDTOWrapperBuilder implements DTOWrapperBuilder {

    /**
     * The 18 n.
     */
    private final I18n i18n;

    /**
     * Instantiates a new Sample dto wrapper builder.
     */
    public SampleDTOWrapperBuilder() {
        this.i18n = Application.getInstance(I18n.class);
    }

    @Override
    public Object build(final HttpServerExchange exchange, final HttpResult result, final Object dataModel) {
        final Locale locale = I18nCookie.resolveAndSetCookie(exchange, result::withCookie);

        return new SampleDTOWrapper<>(dataModel, exchange.getRequestURL(), this.i18n.getLocalizedMessage(locale));
    }
}
