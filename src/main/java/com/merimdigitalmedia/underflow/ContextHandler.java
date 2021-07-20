package com.merimdigitalmedia.underflow;

import com.merimdigitalmedia.underflow.annotation.method.ALL;
import com.merimdigitalmedia.underflow.annotation.method.CUSTOM;
import com.merimdigitalmedia.underflow.annotation.method.DELETE;
import com.merimdigitalmedia.underflow.annotation.method.GET;
import com.merimdigitalmedia.underflow.annotation.method.OPTION;
import com.merimdigitalmedia.underflow.annotation.method.PATCH;
import com.merimdigitalmedia.underflow.annotation.method.POST;
import com.merimdigitalmedia.underflow.annotation.method.PUT;
import com.merimdigitalmedia.underflow.annotation.routing.Name;
import com.merimdigitalmedia.underflow.annotation.routing.Path;
import com.merimdigitalmedia.underflow.annotation.routing.Paths;
import com.merimdigitalmedia.underflow.annotation.routing.Query;
import com.merimdigitalmedia.underflow.converters.Converters;
import com.merimdigitalmedia.underflow.path.PathMatcher;
import com.merimdigitalmedia.underflow.path.PathMatcherBundle;
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
import java.util.regex.Pattern;

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
            if (this.methodMatch(this.exchange, classMethod, annotationForMethod)) {
                final PathMatcherBundle pathMatcherBundle = this.routingMatch(this.exchange, classMethod);
                final PathMatcher matcher = pathMatcherBundle.find();
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

            if (pClass.isAssignableFrom(HttpServerExchange.class)) {
                methodArgs.add(this.exchange);
            } else if (pName != null && this.pathMatcher.hasGroup(pName.value())) {
                final String value = this.pathMatcher.getGroup(pName.value());
                methodArgs.add(Converters.convert(pClass, value));
            } else if (pQuery != null && this.queryParameter.hasParameter(pQuery.value())) {
                final String value = this.queryParameter.getParameter(pQuery.value());
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
     * @param exchange the exchange
     * @param method   the method
     * @return the path matcher
     */
    private PathMatcherBundle routingMatch(final HttpServerExchange exchange,
                                           final Method method) {
        final PathMatcherBundle pathMatcherBundle = new PathMatcherBundle();

        if (method.isAnnotationPresent(Path.class)) {
            final Path path = method.getAnnotation(Path.class);
            pathMatcherBundle.addMatcher(this.makePathMatcher(path));
        } else if (method.isAnnotationPresent(Paths.class)) {
            final Paths paths = method.getAnnotation(Paths.class);
            for (final Path path : paths.value()) {
                pathMatcherBundle.addMatcher(this.makePathMatcher(path));
            }
        }

        return pathMatcherBundle;
    }

    /**
     * Make path matcher path matcher.
     *
     * @param path the path
     * @return the path matcher
     */
    private PathMatcher makePathMatcher(final Path path) {
        if (path.value().isEmpty()) {
            return new PathMatcher(this.exchange.getRelativePath(), Pattern.compile("^$"));
        }
        // (?:/|$) <- non capturing group testing for a / or the end of the string !
        return new PathMatcher(this.exchange.getRelativePath(), path.value());
    }

    /**
     * Is.
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
