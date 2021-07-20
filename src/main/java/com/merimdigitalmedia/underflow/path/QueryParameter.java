package com.merimdigitalmedia.underflow.path;

import com.merimdigitalmedia.underflow.annotation.routing.Query;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * QueryParameter.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class QueryParameter {

    /**
     * The Query parameters.
     */
    private final Map<String, Deque<String>> queryParameters;

    /**
     * The Parameters.
     */
    private final List<Query> parameters;

    /**
     * Instantiates a new Query parameter.
     *
     * @param pathParameters the query parameters
     * @param method         the method
     */
    public QueryParameter(final Map<String, Deque<String>> pathParameters,
                          final Method method) {
        this.queryParameters = new HashMap<>();
        this.parameters = new ArrayList<>();

        for (final Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Query.class)) {
                final Query query = parameter.getAnnotation(Query.class);
                this.parameters.add(query);
                this.queryParameters.put(query.value(), pathParameters.get(query.value()));
            }
        }
    }

    /**
     * Check that all requested parameters are present in the query.
     *
     * @return true if all requested parameters are persent
     */
    public boolean checkRequired() {
        for (final Query parameter : this.parameters) {
            if (parameter.required()
                    && this.queryParameters.get(parameter.value()) == null) {
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
        final Deque<String> value = this.queryParameters.get(parameterName);
        return value == null ? null : value.element();
    }
}
