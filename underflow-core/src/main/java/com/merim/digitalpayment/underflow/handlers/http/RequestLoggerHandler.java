package com.merim.digitalpayment.underflow.handlers.http;

import com.merim.digitalpayment.underflow.handlers.PassthroughsHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * RequestLoggerHandler.
 *
 * @author Pierre Adam
 * @since 22.07.04
 */
public class RequestLoggerHandler extends PassthroughsHandler {

    /**
     * The Logger.
     */
    private final Logger logger;

    /**
     * The Filter.
     */
    private final Function<HttpServerExchange, Boolean> filter;

    /**
     * Instantiates a new Logging handler.
     *
     * @param underlying the underlying handler
     */
    public RequestLoggerHandler(final HttpHandler underlying) {
        this(underlying, null, null);
    }

    /**
     * Instantiates a new Logging handler.
     *
     * @param underlying the underlying handler
     * @param loggerName the logger name
     */
    public RequestLoggerHandler(final HttpHandler underlying, final String loggerName) {
        this(underlying, null, loggerName);
    }

    /**
     * Instantiates a new Request logger handler.
     *
     * @param underlying the underlying
     * @param filter     the filter
     */
    public RequestLoggerHandler(final HttpHandler underlying,
                                final Function<HttpServerExchange, Boolean> filter) {
        this(underlying, filter, null);
    }

    /**
     * Instantiates a new Request logger handler.
     *
     * @param underlying the underlying
     * @param filter     the filter
     * @param loggerName the logger name
     */
    public RequestLoggerHandler(final HttpHandler underlying,
                                final Function<HttpServerExchange, Boolean> filter,
                                final String loggerName) {
        super(underlying);
        this.filter = filter != null ? filter : exchange -> true;
        this.logger = loggerName != null ? LoggerFactory.getLogger(loggerName) :
                LoggerFactory.getLogger(this.getFinalBackedHandler().getClass());
    }

    @Override
    protected void interceptRequest(final HttpServerExchange exchange) {
        if (this.filter.apply(exchange)) {
            final String queryString = exchange.getQueryString().isEmpty() ? "" : "?" + exchange.getQueryString();

            this.logger.info("[{}] {}{}", exchange.getRequestMethod(), exchange.getRequestURL(), queryString);
        }
    }
}
