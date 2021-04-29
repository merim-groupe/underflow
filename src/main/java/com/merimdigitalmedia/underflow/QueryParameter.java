package com.merimdigitalmedia.underflow;

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

    /**
     * The constant NO_PARAMETERS.
     */
    private static final QueryParameter NO_PARAMETERS;

    static {
        NO_PARAMETERS = new QueryParameter(new HashMap<>(), new String[]{});
    }

    /**
     * The Query parameters.
     */
    private final Map<String, Deque<String>> queryParameters;

    /**
     * The Parameters.
     */
    private final String[] parameters;

    /**
     * Instantiates a new Query parameter.
     *
     * @param queryParameters the query parameters
     * @param parameters      the parameters
     */
    public QueryParameter(final Map<String, Deque<String>> queryParameters,
                          final String[] parameters) {
        this.queryParameters = queryParameters;
        this.parameters = parameters;
    }

    /**
     * Gets no query parameter instance.
     *
     * @return the query parameter
     */
    public static QueryParameter noParameters() {
        return QueryParameter.NO_PARAMETERS;
    }

    /**
     * Check that all requested parameters are present in the query.
     *
     * @return true if all requested parameters are persent
     */
    public boolean arePresents() {
        for (final String parameter : this.parameters) {
            if (!this.queryParameters.containsKey(parameter)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the query parameters contains the given parameter.
     *
     * @param parameterName the parameter name
     * @return the boolean
     */
    public boolean hasParameter(final String parameterName) {
        return this.queryParameters.containsKey(parameterName);
    }

    /**
     * Gets the value of the query parameter.
     *
     * @param parameterName the parameter name
     * @return the parameter
     */
    public String getParameter(final String parameterName) {
        return this.queryParameters.get(parameterName).element();
    }
}
