package com.merimdigitalmedia.underflow.handlers.context;

import com.merimdigitalmedia.underflow.annotation.io.Dispatch;
import com.merimdigitalmedia.underflow.annotation.method.*;
import com.merimdigitalmedia.underflow.annotation.routing.*;
import com.merimdigitalmedia.underflow.annotation.security.Secured;
import com.merimdigitalmedia.underflow.converters.Converters;
import com.merimdigitalmedia.underflow.handlers.context.path.PathMatcher;
import com.merimdigitalmedia.underflow.handlers.context.path.QueryString;
import com.merimdigitalmedia.underflow.handlers.flows.FlowHandler;
import com.merimdigitalmedia.underflow.mdc.MDCContext;
import com.merimdigitalmedia.underflow.results.Result;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
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
public class ContextHandler implements MDCContext {

    /**
     * The Logger.
     */
    private final Logger logger;

    /**
     * The Handler.
     */
    private final FlowHandler handler;

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
     * The Controller injectable.
     */
    private final Map<Class<?>, Object> controllerInjectable;

    /**
     * Instantiates a new Context handler.
     *
     * @param handler  the handler
     * @param exchange the exchange
     */
    public ContextHandler(final FlowHandler handler,
                          final HttpServerExchange exchange) {
        this.logger = LoggerFactory.getLogger(ContextHandler.class);
        this.handler = handler;
        this.exchange = exchange;
        this.method = null;
        this.pathMatcher = null;
        this.queryString = null;
        this.controllerInjectable = new HashMap<>();
    }

    /**
     * Check that there is a valid method to handle the call.
     *
     * @return true if there is a valid method
     */
    public boolean isValid() {
        final Class<? extends Annotation> annotationForMethod = this.getAnnotationForMethod(this.exchange.getRequestMethod());

        for (final Method classMethod : this.handler.getClass().getMethods()) {
            if (classMethod.getReturnType().isAssignableFrom(Result.class) && this.methodMatch(classMethod, annotationForMethod)) {
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
     * Require security.
     *
     * @return the boolean
     */
    public Optional<Secured> requireSecurity() {
        return Optional.ofNullable(this.method.getAnnotation(Secured.class));
    }

    /**
     * Gets method.
     *
     * @return the method
     */
    public Method getMethod() {
        return this.method;
    }

    /**
     * Check that there is a fallback method for the call.
     *
     * @param aClass the class of the controller
     * @return the boolean
     */
    private boolean hasFallbackMethod(final Class<?> aClass) {
        for (final Method classMethod : aClass.getMethods()) {
            if (classMethod.isAnnotationPresent(Fallback.class) && classMethod.getReturnType().isAssignableFrom(Result.class)) {
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
        final Optional<Dispatch> optionalDispatch = Optional.ofNullable(this.method.getAnnotation(Dispatch.class));

        for (final Parameter parameter : this.method.getParameters()) {
            final Class<?> pClass = parameter.getType();
            final Named pNamed = parameter.getAnnotation(Named.class);
            final Query pQuery = parameter.getAnnotation(Query.class);

            if (pClass.isAssignableFrom(HttpServerExchange.class)) {
                methodArgs.add(this.exchange);
            } else if (this.controllerInjectable.containsKey(pClass)) {
                methodArgs.add(this.controllerInjectable.get(pClass));
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

        if (optionalDispatch.isPresent() && this.exchange.isInIoThread()) {
            final Map<String, String> mdcContext = this.popMDCContext();
            final Dispatch dispatch = optionalDispatch.get();

            this.exchange.dispatch(() -> this.safeHttpExecute(() -> this.withMDCContext(mdcContext, () -> {
                if (dispatch.block() && !this.exchange.isBlocking()) {
                    // Closable to fix after migration to Java 9+.
                    this.exchange.startBlocking();
                }

                this.executeMethod(methodArgs);
            })));
        } else {
            this.safeHttpExecute(() -> this.executeMethod(methodArgs));
        }
    }

    /**
     * As optional or object object.
     *
     * @param pClass the p class
     * @param value  the value
     * @return the object
     */
    private Object asOptionalOrObject(final Class<?> pClass, final Object value) {
        if (pClass.isAssignableFrom(Optional.class)) {
            return Optional.ofNullable(value);
        } else {
            return value;
        }
    }

    /**
     * Safe http execute.
     *
     * @param logic the logic
     */
    private void safeHttpExecute(final Runnable logic) {
        try {
            logic.run();
        } catch (final Exception e) {
            this.logger.error("Something wrong happened.", e);
            if (!this.exchange.isResponseStarted()) {
                this.exchange.setStatusCode(500);
            }
            this.exchange.endExchange();
        }
    }

    /**
     * Execute method.
     *
     * @param methodArgs the method args
     */
    private void executeMethod(final List<Object> methodArgs) {
        try {
            final Result result = (Result) this.method.invoke(this.handler, methodArgs.toArray());
            result.process(this.exchange);
        } catch (final Throwable e) {
            Throwable cause = e;
            if (e instanceof InvocationTargetException) {
                cause = e.getCause();
            }
            this.logger.error("Controller uncaught exception.", cause);
            try {
                this.handler.onException(cause).process(this.exchange);
            } catch (final Exception e2) {
                throw new RuntimeException(e2);
            }
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

    /**
     * Add injectable.
     *
     * @param <T>   the type parameter
     * @param value the value
     */
    public <T> void addInjectable(final T value) {
        if (value != null) {
            this.controllerInjectable.put(value.getClass(), value);
        }
    }

    /**
     * Add injectable.
     *
     * @param <T>    the type parameter
     * @param tClass the t class
     * @param value  the value
     */
    public <T> void addInjectable(final Class<T> tClass, final T value) {
        this.controllerInjectable.put(tClass, value);
    }

    /**
     * Add injectable.
     *
     * @param tClass the t class
     * @param value  the value
     */
    public void addInjectableUnsafe(final Class<?> tClass, final Object value) {
        this.controllerInjectable.put(tClass, value);
    }
}
