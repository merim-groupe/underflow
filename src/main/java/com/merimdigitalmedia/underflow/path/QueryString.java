package com.merimdigitalmedia.underflow.path;

import com.merimdigitalmedia.underflow.annotation.routing.Query;
import com.merimdigitalmedia.underflow.annotation.routing.QueryListProperty;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * QueryString.
 *
 * @author Lucas Stadelmann
 * @since 21.04.28
 */
public class QueryString {

    /**
     * The Query representation.
     */
    private final Map<String, Deque<String>> query;

    /**
     * The Properties.
     */
    private final List<Query> properties;

    /**
     * Instantiates a new Query parameter.
     *
     * @param pathParameters the query parameters
     * @param method         the method
     */
    public QueryString(final Map<String, Deque<String>> pathParameters,
                       final Method method) {
        this.query = new HashMap<>();
        this.properties = new ArrayList<>();

        for (final Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Query.class)) {
                final Query query = parameter.getAnnotation(Query.class);
                this.properties.add(query);
                if (query.listProperty().backedType().equals(QueryListProperty.NoBackedType.class)) {
                    this.query.put(query.value(), pathParameters.get(query.value()));
                } else {
                    final String format = String.format("%s\\[\\d*\\]", query.value());
                    final Pattern pattern = Pattern.compile(format);

                    if (pathParameters.containsKey(query.value())) {
                        this.query.put(query.value(), pathParameters.get(query.value()));
                    }

                    for (final String key : pathParameters.keySet()) {
                        if (pattern.matcher(key).matches()) {
                            if (this.query.containsKey(query.value())) {
                                this.query.get(query.value()).addAll(pathParameters.get(key));
                            } else {
                                this.query.put(query.value(), pathParameters.get(key));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check that all requested parameters are present in the query.
     *
     * @return true if all requested parameters are persent
     */
    public boolean checkRequired() {
        for (final Query parameter : this.properties) {
            if (parameter.required()
                    && this.query.get(parameter.value()) == null) {
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
    public boolean hasKey(final String parameterName) {
        return this.query.containsKey(parameterName);
    }

    /**
     * Gets values for.
     *
     * @param parameterName the parameter name
     * @return the values for
     */
    public Deque<String> getValuesFor(final String parameterName) {
        final Deque<String> values = this.query.get(parameterName);
        return values == null ? new ArrayDeque<>() : values;
    }
}
