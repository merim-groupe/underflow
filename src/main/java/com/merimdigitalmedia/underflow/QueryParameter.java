package com.merimdigitalmedia.underflow;

import io.undertow.server.HttpServerExchange;

import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * QueryParameter.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class QueryParameter {

    private static final QueryParameter NO_PARAMETERS;

    static {
        NO_PARAMETERS = new QueryParameter(new HashMap<>());
    }

    private final Map<String, Deque<String>> queryParameters;

    private QueryParameter(final Map<String, Deque<String>> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public QueryParameter(final HttpServerExchange exchange) {
        this(exchange.getQueryParameters());
    }

    public static QueryParameter noParameters() {
        return QueryParameter.NO_PARAMETERS;
    }

    public boolean hasParameter(final String parameterName) {
        return this.queryParameters.containsKey(parameterName);
    }

    public String getParameter(final String parameterName) {
        return this.queryParameters.get(parameterName).element();
    }
}
