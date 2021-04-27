package com.merimdigitalmedia.underflow;

import com.merimdigitalmedia.underflow.annotation.method.*;
import com.merimdigitalmedia.underflow.annotation.routing.Name;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * HandlerContext.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class ContextHandler {

    private final Object handler;

    private final HttpServerExchange exchange;

    private Method method;

    private PathMatcher pathMatcher;

    public ContextHandler(final Object handler, final HttpServerExchange exchange) {
        this.handler = handler;
        this.exchange = exchange;
        this.method = null;
        this.pathMatcher = null;
    }

    public boolean isValid() {
        final Class<? extends Annotation> annotationForMethod = this.getAnnotationForMethod(this.exchange.getRequestMethod());

        for (final Method method : this.handler.getClass().getMethods()) {
            if (this.methodMatch(this.exchange, method, annotationForMethod)) {
                final PathMatcher pathMatcher = this.routingMatch(this.exchange, method);
                if (pathMatcher.find()) {
                    this.method = method;
                    this.pathMatcher = pathMatcher;
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Execute.
     */
    void execute() {
        this.exchange.setRelativePath(this.pathMatcher.getRemaining());
        final List<Object> methodArgs = new ArrayList<>();

        final Parameter[] parameters = this.method.getParameters();
        for (final Parameter parameter : parameters) {
            final Class<?> pClass = parameter.getType();
            final Name pName = parameter.getAnnotation(Name.class);

            if (pClass.isAssignableFrom(HttpServerExchange.class)) {
                methodArgs.add(this.exchange);
            } else {
                final String groupName = pName.value();
                final String value = this.pathMatcher.getGroup(groupName);

                methodArgs.add(FlowConverters.convert(pClass, value));
            }
        }
        try {
            this.method.invoke(this.handler, methodArgs.toArray());
        } catch (final IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Routing match path matcher.
     *
     * @param exchange the exchange
     * @param method   the method
     * @return the path matcher
     */
    private PathMatcher routingMatch(final HttpServerExchange exchange, final Method method) {
        if (method.isAnnotationPresent(Path.class)) {
            final Path path = method.getAnnotation(Path.class);
            return new PathMatcher(exchange.getRelativePath(), Pattern.compile(String.format("^%s", path.value())));
        }

        return PathMatcher.noMatch();
    }

    /**
     * Method match boolean.
     *
     * @param exchange            the exchange
     * @param method              the method
     * @param annotationForMethod the annotation for method
     * @return the boolean
     */
    private boolean methodMatch(final HttpServerExchange exchange,
                                final Method method,
                                final Class<? extends Annotation> annotationForMethod) {
        if ((annotationForMethod != null && method.isAnnotationPresent(annotationForMethod))
                || method.isAnnotationPresent(ALL.class)) {
            return true;
        }
        if (method.isAnnotationPresent(CUSTOM.class)) {
            final CUSTOM httpMethod = method.getAnnotation(CUSTOM.class);
            return httpMethod.value().equals(exchange.getRequestMethod().toString());
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
            case "OPTION":
                return OPTION.class;
            case "DELETE":
                return DELETE.class;
            default:
                return null;
        }
    }
}
