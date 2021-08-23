package com.merimdigitalmedia.underflow;

import com.merimdigitalmedia.underflow.annotation.method.*;
import com.merimdigitalmedia.underflow.annotation.routing.*;
import com.merimdigitalmedia.underflow.converters.Converters;
import com.merimdigitalmedia.underflow.path.PathMatcher;
import com.merimdigitalmedia.underflow.path.QueryParameter;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * HandlerContext.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class ContextHandler {

    /**
     * The Handler.
     */
    private final Object handler;

    /**
     * The Exchange.
     */
    private final HttpServerExchange exchange;

    /**
     * The Method.
     */
    private Method method;

    /**
     * The Path matcher.
     */
    private PathMatcher pathMatcher;

    /**
     * The Query parameter.
     */
    private QueryParameter queryParameter;

    /**
     * Instantiates a new Context handler.
     *
     * @param handler  the handler
     * @param exchange the exchange
     */
    public ContextHandler(final Object handler,
                          final HttpServerExchange exchange) {
        this.handler = handler;
        this.exchange = exchange;
        this.method = null;
        this.pathMatcher = null;
        this.queryParameter = null;
    }

    /**
     * Check that there is a valid method to handle the call.
     *
     * @return true if there is a valid method
     */
    public boolean isValid() {
        final Class<? extends Annotation> annotationForMethod = this.getAnnotationForMethod(this.exchange.getRequestMethod());

        for (final Method classMethod : this.handler.getClass().getMethods()) {
            if (this.methodMatch(classMethod, annotationForMethod)) {
                final PathMatcher matcher = this.getPathMatcher(classMethod);
                final QueryParameter parameter = new QueryParameter(this.exchange.getQueryParameters(), classMethod);
                if (matcher.find() && parameter.checkRequired()) {
                    this.method = classMethod;
                    this.pathMatcher = matcher;
                    this.queryParameter = parameter;

                    return true;
                }
            }
        }

        return this.hasFallbackMethod(this.handler.getClass());
    }

    /**
     * Check that there is a fallback method for the call.
     *
     * @param aClass the a class
     * @return the boolean
     */
    private boolean hasFallbackMethod(final Class<?> aClass) {
        for (final Method classMethod : aClass.getMethods()) {
            if (classMethod.isAnnotationPresent(Fallback.class)) {

                final PathMatcher matcher = new PathMatcher(this.exchange.getRelativePath(), ".*", true);
                final QueryParameter parameter = new QueryParameter(this.exchange.getQueryParameters(), classMethod);

                if (matcher.find() && parameter.checkRequired()) {
                    this.method = classMethod;
                    this.pathMatcher = matcher;
                    this.queryParameter = parameter;

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Resolve the arguments and execute de method.
     */
    void execute() {
        this.exchange.setRelativePath(this.pathMatcher.getRemainingPath());
        final List<Object> methodArgs = new ArrayList<>();

        for (final Parameter parameter : this.method.getParameters()) {
            final Class<?> pClass = parameter.getType();
            final Name pName = parameter.getAnnotation(Name.class);
            final Query pQuery = parameter.getAnnotation(Query.class);
            final DefaultValue pDefaultValue = parameter.getAnnotation(DefaultValue.class);

            if (pClass.isAssignableFrom(HttpServerExchange.class)) {
                methodArgs.add(this.exchange);
            } else if (pName != null && this.pathMatcher.hasGroup(pName.value())) {
                final String value = this.pathMatcher.getGroup(pName.value());
                methodArgs.add(Converters.convert(pClass, value));
            } else if (pQuery != null && this.queryParameter.hasParameter(pQuery.value())) {
                String value = this.queryParameter.getParameter(pQuery.value());
                if (value == null && pDefaultValue != null) {
                    value = pDefaultValue.value();
                }
                methodArgs.add(Converters.convert(pClass, value));
            }
        }
        try {
            this.method.invoke(this.handler, methodArgs.toArray());
        } catch (final IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets path matcher for the given path pattern.
     *
     * @param method the method
     * @return the path matcher
     */
    private PathMatcher getPathMatcher(final Method method) {
        if (method.isAnnotationPresent(Path.class)) {
            final Path path = method.getAnnotation(Path.class);
            return new PathMatcher(this.exchange.getRelativePath(), path);
        } else if (method.isAnnotationPresent(Paths.class)) {
            final Paths paths = method.getAnnotation(Paths.class);
            for (final Path path : paths.value()) {
                final PathMatcher pathMatcher = new PathMatcher(this.exchange.getRelativePath(), path);
                if (pathMatcher.find()) {
                    pathMatcher.reset();
                    return pathMatcher;
                }
            }
        }
        return PathMatcher.noMatch();
    }

    /**
     * Is.
     *
     * @param method              the method
     * @param annotationForMethod the annotation for method
     * @return the boolean
     */
    private boolean methodMatch(final Method method,
                                final Class<? extends Annotation> annotationForMethod) {
        if ((annotationForMethod != null && method.isAnnotationPresent(annotationForMethod))
                || method.isAnnotationPresent(ALL.class)) {
            return true;
        }
        if (method.isAnnotationPresent(CUSTOM.class)) {
            final CUSTOM httpMethod = method.getAnnotation(CUSTOM.class);
            return httpMethod.value().equals(this.exchange.getRequestMethod().toString());
        }
        return false;
    }

    /**
     * Gets annotation for method.
     *
     * @param httpMethod the http method
     * @return the annotation for method
     */
    private Class<? extends Annotation> getAnnotationForMethod(final HttpString httpMethod) {
        switch (httpMethod.toString().toUpperCase(Locale.ROOT)) {
            case "GET":
                return GET.class;
            case "POST":
                return POST.class;
            case "PATCH":
                return PATCH.class;
            case "PUT":
                return PUT.class;
            case "DELETE":
                return DELETE.class;
            case "OPTIONS":
                return OPTIONS.class;
            case "HEAD":
                return HEAD.class;
            default:
                return null;
        }
    }
}
