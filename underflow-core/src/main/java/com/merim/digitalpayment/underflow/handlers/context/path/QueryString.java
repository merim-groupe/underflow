package com.merim.digitalpayment.underflow.handlers.context.path;

import com.merim.digitalpayment.underflow.annotation.AnnotationResolver;
import com.merim.digitalpayment.underflow.annotation.routing.QueryParamList;
import com.merim.digitalpayment.underflow.annotation.routing.QueryParamRequired;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Pattern;

/**
 * QueryString.
 *
 * @author Pierre Adam
 * @since 21.04.28
 */
public class QueryString {

    /**
     * The Query representation.
     */
    private final Map<String, QueryStringEntry> queryEntries;

    /**
     * Instantiates a new Query parameter.
     *
     * @param pathParameters the query parameters
     * @param method         the method
     */
    public QueryString(final Map<String, Deque<String>> pathParameters,
                       final Method method) {
        this.queryEntries = new HashMap<>();

        for (final Parameter parameter : method.getParameters()) {
            final Optional<QueryParam> oQueryParam = AnnotationResolver.nestedAnnotation(parameter, QueryParam.class);

            if (oQueryParam.isPresent()) {
                final Optional<QueryParamList> oQueryParamList = AnnotationResolver.nestedAnnotation(parameter, QueryParamList.class);
                final Optional<QueryParamRequired> oQueryParamRequired = AnnotationResolver.nestedAnnotation(parameter, QueryParamRequired.class);
                final Optional<DefaultValue> oDefaultValue = AnnotationResolver.nestedAnnotation(parameter, DefaultValue.class);

                final QueryStringEntry entry = new QueryStringEntry(oQueryParamRequired.isPresent(), oDefaultValue.orElse(null));
                final String queryParamName = oQueryParam.get().value();
                this.queryEntries.put(queryParamName, entry);

                if (pathParameters.containsKey(queryParamName)) {
                    entry.getData().addAll(pathParameters.get(queryParamName));
                }

                if (oQueryParamList.isPresent()) {
                    final String format = String.format("%s\\[\\d*\\]", queryParamName);
                    final Pattern pattern = Pattern.compile(format);

                    for (final String key : pathParameters.keySet()) {
                        if (pattern.matcher(key).matches()) {
                            entry.getData().addAll(pathParameters.get(key));
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
        for (final QueryStringEntry entry : this.queryEntries.values()) {
            if (entry.isRequired() && entry.getDataOrDefault().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check if the query parameters contain the given parameter.
     *
     * @param parameterName the parameter name
     * @return the boolean
     */
    public boolean hasKey(final String parameterName) {
        return this.queryEntries.containsKey(parameterName);
    }

    /**
     * Gets values for.
     *
     * @param parameterName the parameter name
     * @return the values for
     */
    public Deque<String> getValuesFor(final String parameterName) {
        if (this.queryEntries.containsKey(parameterName)) {
            return this.queryEntries.get(parameterName).getDataOrDefault();
        }

        return new ArrayDeque<>();
    }
}
