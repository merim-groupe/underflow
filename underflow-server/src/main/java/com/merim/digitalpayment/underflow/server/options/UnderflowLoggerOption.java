package com.merim.digitalpayment.underflow.server.options;

import com.merim.digitalpayment.underflow.handlers.http.RequestLoggerHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import lombok.NonNull;

import java.util.function.Function;

/**
 * UnderflowLoggerOption.
 *
 * @author Pierre Adam
 * @since 24.04.23
 */
public class UnderflowLoggerOption implements UnderflowOption {

    /**
     * The constant LOG_ALL_QUERY.
     */
    public static final UnderflowLoggerOption LOG_ALL_QUERY = new UnderflowLoggerOption(e -> true);

    /**
     * The Enabled.
     */
    private final Function<HttpServerExchange, Boolean> filter;

    /**
     * Instantiates a new Underflow logger option.
     *
     * @param filter the filter
     */
    public UnderflowLoggerOption(@NonNull final Function<HttpServerExchange, Boolean> filter) {
        this.filter = filter;
    }

    @Override
    public HttpHandler alterHandler(final String path, final HttpHandler handler) {
        return new RequestLoggerHandler(handler, this.filter);
    }
}
