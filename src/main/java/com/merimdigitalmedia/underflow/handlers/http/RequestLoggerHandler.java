package com.merimdigitalmedia.underflow.handlers.http;

import com.merimdigitalmedia.underflow.handlers.PassthroughsHandler;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * Instantiates a new Logging handler.
     *
     * @param underlying the underlying handler
     */
    public RequestLoggerHandler(final HttpHandler underlying) {
        super(underlying);
        this.logger = LoggerFactory.getLogger(this.getFinalBackedHandler().getClass());
    }

    /**
     * Instantiates a new Logging handler.
     *
     * @param underlying the underlying handler
     * @param loggerName the logger name
     */
    public RequestLoggerHandler(final HttpHandler underlying, final String loggerName) {
        super(underlying);
        this.logger = LoggerFactory.getLogger(loggerName);
    }

    @Override
    protected void interceptRequest(final HttpServerExchange exchange) {
        final String queryString = exchange.getQueryString().isEmpty() ? "" : "?" + exchange.getQueryString();
        
        this.logger.info("[{}] {}{}", exchange.getRequestMethod(), exchange.getRequestURL(), queryString);
    }
}
