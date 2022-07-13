package com.merimdigitalmedia.underflow.handlers.context;

import com.merimdigitalmedia.underflow.annotation.method.*;
import com.merimdigitalmedia.underflow.annotation.routing.*;
import com.merimdigitalmedia.underflow.converters.Converters;
import com.merimdigitalmedia.underflow.path.PathMatcher;
import com.merimdigitalmedia.underflow.path.QueryString;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

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
    private QueryString queryString;

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
        this.queryString = null;
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
                final QueryString parameter = new QueryString(this.exchange.getQueryParameters(), classMethod);
                if (matcher.find() && parameter.checkRequired()) {
                    this.method = classMethod;
                    this.pathMatcher = matcher;
                    this.queryString = parameter;

                    return true;
                }
            }
        }

        return this.hasFallbackMethod(this.handler.getClass());
    }

    /**
     * Check that there is a fallback method for the call.
     *
     * @param aClass the class of the controller
     * @return the boolean
     */
    private boolean hasFallbackMethod(final Class<?> aClass) {
        for (final Method classMethod : aClass.getMethods()) {
            if (classMethod.isAnnotationPresent(Fallback.class)) {

                final PathMatcher matcher = new PathMatcher(this.exchange.getRelativePath(), ".*", true);
                final QueryString parameter = new QueryString(this.exchange.getQueryParameters(), classMethod);

                if (matcher.find() && parameter.checkRequired()) {
                    this.method = classMethod;
                    this.pathMatcher = matcher;
                    this.queryString = parameter;

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Resolve the arguments and execute de method.
     */
    public void execute() {
        this.exchange.setRelativePath(this.pathMatcher.getRemainingPath());
        final List<Object> methodArgs = new ArrayList<>();

        for (final Parameter parameter : this.method.getParameters()) {
            final Class<?> pClass = parameter.getType();
            final Named pNamed = parameter.getAnnotation(Named.class);
            final Query pQuery = parameter.getAnnotation(Query.class);

            if (pClass.isAssignableFrom(HttpServerExchange.class)) {
                methodArgs.add(this.exchange);
            } else if (pNamed != null && this.pathMatcher.hasGroup(pNamed.value())) {
                final String value = this.pathMatcher.getGroup(pNamed.value());
                methodArgs.add(Converters.convert(pClass, value));
            } else if (pQuery != null) {
                final Deque<String> values = this.queryString.getValuesFor(pQuery.value());
                if (values.isEmpty() && pQuery.defaultValue().value().length > 0) {
                    values.addAll(Arrays.asList(pQuery.defaultValue().value()));
                }
                if (pQuery.listProperty().backedType() != QueryListProperty.NoBackedType.class) {
                    final Class<?> backedType = pQuery.listProperty().backedType();
                    final List<Object> list = values.stream().map(v -> Converters.convert(backedType, v)).collect(Collectors.toList());
                    methodArgs.add(list);
                } else {
                    if (values.isEmpty()) {
                        methodArgs.add(null);
                    } else {
                        methodArgs.add(Converters.convert(pClass, values.getFirst()));
                    }
                }
            } else {
                LoggerFactory.getLogger(this.handler.getClass()).warn("Unable to resolve the argument <{}@{}> for the method {}." +
                                "Please use the annotation @Named or @Query to specify how to resolve this argument.",
                        parameter.getName(), pClass.getCanonicalName(), this.method.getName());
                methodArgs.add(null);
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
