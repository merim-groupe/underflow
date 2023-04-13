package com.merim.digitalpayment.underflow.handlers.context;

import com.merim.digitalpayment.underflow.annotation.method.*;
import com.merim.digitalpayment.underflow.annotation.routing.*;
import com.merim.digitalpayment.underflow.annotation.security.Secured;
import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.converters.Converters;
import com.merim.digitalpayment.underflow.converters.IConverter;
import com.merim.digitalpayment.underflow.converters.NoConverter;
import com.merim.digitalpayment.underflow.enums.MethodType;
import com.merim.digitalpayment.underflow.handlers.context.path.PathMatcher;
import com.merim.digitalpayment.underflow.handlers.context.path.QueryString;
import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
import com.merim.digitalpayment.underflow.mdc.MDCContext;
import com.merim.digitalpayment.underflow.results.Result;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.util.HttpString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Supplier;
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
     * The Controller injectable.
     */
    private final Map<Class<?>, Supplier<Object>> controllerInjectable;

    /**
     * The Controller injectable.
     */
    private final Map<Class<?>, IConverter<?>> runtimeConverter;

    /**
     * The Method type.
     */
    private MethodType methodType;

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
    public ContextHandler(final FlowHandler handler,
                          final HttpServerExchange exchange) {
        this.logger = LoggerFactory.getLogger(ContextHandler.class);
        this.handler = handler;
        this.exchange = exchange;
        this.methodType = MethodType.UNSUPPORTED;
        this.method = null;
        this.pathMatcher = null;
        this.queryString = null;
        this.controllerInjectable = new HashMap<>();
        this.runtimeConverter = new HashMap<>();
        this.controllerInjectable.put(FormData.class, () -> this.getFormData(this.exchange));
        this.controllerInjectable.put(HttpServerExchange.class, () -> this.exchange);
    }

    /**
     * Check that there is a valid method to handle the call.
     *
     * @return true if there is a valid method
     */
    public boolean isValid() {
        final Class<? extends Annotation> annotationForMethod = this.getAnnotationForMethod(this.exchange.getRequestMethod());
        final Class<? extends FlowHandler> hClass = this.handler.getClass();

        for (final Method classMethod : hClass.getMethods()) {
            final MethodType type = MethodType.resolve(classMethod);
            if (type != MethodType.UNSUPPORTED && this.methodMatch(classMethod, annotationForMethod)) {
                final PathMatcher matcher = this.getPathMatcher(hClass, classMethod, type);
                final QueryString parameter = new QueryString(this.exchange.getQueryParameters(), classMethod);
                if (matcher.find() && parameter.checkRequired()) {
                    // Try to find "a match" or "the best match".
                    if (this.method == null || matcher.getRemainingPath().length() < this.pathMatcher.getRemainingPath().length()) {
                        this.method = classMethod;
                        this.methodType = type;
                        this.pathMatcher = matcher;
                        this.queryString = parameter;
                    }
                }
            }
        }

        if (this.method != null) {
            return true;
        }

        return this.hasFallbackMethod(hClass);
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
            final MethodType type = MethodType.resolve(classMethod);
            if (classMethod.isAnnotationPresent(Fallback.class) && type != MethodType.UNSUPPORTED) {
                final PathMatcher matcher = new PathMatcher(this.exchange.getRelativePath(), ".*", true);
                final QueryString parameter = new QueryString(this.exchange.getQueryParameters(), classMethod);

                if (matcher.find() && parameter.checkRequired()) {
                    this.method = classMethod;
                    this.methodType = type;
                    this.pathMatcher = matcher;
                    this.queryString = parameter;

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Dispatch if necessary and run.
     */
    public void execute() {
        if (this.exchange.isInIoThread()) {
            final Map<String, String> mdcContext = this.popMDCContext();

            this.exchange.dispatch(() -> this.withMDCContext(mdcContext, this::execute));
        } else {
            if (!this.exchange.isBlocking()) {
                // Closable to fix after migration to Java 9+.
                this.exchange.startBlocking();
            }
            this.run();
        }
    }

    /**
     * Resolve the arguments and execute de method.
     */
    private void run() {
        try {
            this.exchange.setRelativePath(this.pathMatcher.getRemainingPath());

            if (this.methodHasBody()) {
                this.controllerInjectable.put(InputStream.class, this.exchange::getInputStream);
            }

            final List<Object> methodArgs = this.resolveMethodArgs();

            this.safeHttpExecute(() -> this.executeMethod(methodArgs));
        } catch (final Exception e) {
            this.logger.error("An error occurred.", e);

            // This is an error in Underflow itself. Setting status code to 500 and ending the exchange.
            this.exchange.setStatusCode(500);
            this.exchange.endExchange();
        }
    }

    /**
     * Resolve method args list.
     *
     * @return the list
     */
    private List<Object> resolveMethodArgs() {
        final List<Object> methodArgs = new ArrayList<>();

        for (final Parameter parameter : this.method.getParameters()) {
            final Class<?> pClass = parameter.getType();
            final Named pNamed = parameter.getAnnotation(Named.class);
            final Query pQuery = parameter.getAnnotation(Query.class);
            final Optional<?> appInject = Application.getInstanceOptional(pClass);

            if (pNamed != null && this.pathMatcher.hasGroup(pNamed.value())) {
                final String value = this.pathMatcher.getGroup(pNamed.value());
                methodArgs.add(this.queryConvert(pNamed.converter(), pClass, value));
            } else if (pQuery != null) {
                final Deque<String> values = this.queryString.getValuesFor(pQuery.value());
                if (values.isEmpty() && pQuery.defaultValue().value().length > 0) {
                    values.addAll(Arrays.asList(pQuery.defaultValue().value()));
                }
                if (pQuery.listProperty().backedType() != QueryListProperty.NoBackedType.class) {
                    final Class<?> backedType = pQuery.listProperty().backedType();
                    final List<java.lang.Object> list = values.stream()
                            .map(v -> this.queryConvert(pQuery.converter(), backedType, v))
                            .collect(Collectors.toList());
                    methodArgs.add(list);
                } else {
                    if (values.isEmpty()) {
                        methodArgs.add(null);
                    } else {
                        methodArgs.add(this.queryConvert(pQuery.converter(), pClass, values.getFirst()));
                    }
                }
            } else if (this.controllerInjectable.containsKey(pClass)) {
                methodArgs.add(this.controllerInjectable.get(pClass).get());
            } else if (appInject.isPresent()) {
                methodArgs.add(appInject.get());
            } else {
                LoggerFactory.getLogger(this.handler.getClass()).warn("Unable to resolve the argument <{}@{}> for the method {}." +
                                "Please use the annotation @Named or @Query to specify how to resolve this argument.",
                        parameter.getName(), pClass.getCanonicalName(), this.method.getName());
                methodArgs.add(null);
            }
        }

        return methodArgs;
    }

    /**
     * Query arg convert object.
     *
     * @param queryConverter the query converter
     * @param pClass         the p class
     * @param value          the value
     * @return the object
     */
    private Object queryConvert(final Converter queryConverter, final Class<?> pClass, final String value) {
        if (queryConverter != null && !queryConverter.value().equals(NoConverter.class)) {
            if (!this.runtimeConverter.containsKey(queryConverter.value())) {
                try {
                    this.runtimeConverter.put(queryConverter.value(),
                            queryConverter.value().getDeclaredConstructor().newInstance());
                } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    this.logger.error("Unable to instantiate the converter {}", queryConverter.value().getCanonicalName());
                    throw new RuntimeException(e);
                }
            }

            final IConverter<?> converter = this.runtimeConverter.get(queryConverter.value());

            if (!pClass.isAssignableFrom(converter.getBackedType())) {
                this.logger.error("Invalid converter. Converter {} is able to handle {} but {} was given as argument.",
                        converter.getClass().getCanonicalName(), converter.getBackedType().getCanonicalName(),
                        pClass.getCanonicalName());
                throw new RuntimeException("Invalid converter.");
            }
            return converter.bind(value);
        } else {
            return Converters.convert(pClass, value);
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
            if (this.methodType == MethodType.RESULT) {
                final Result result = (Result) this.method.invoke(this.handler, methodArgs.toArray());
                result.process(this.exchange);
            } else if (this.methodType == MethodType.HANDLER) {
                final FlowHandler subHandler = (FlowHandler) this.method.invoke(this.handler, methodArgs.toArray());
                subHandler.handleRequest(this.exchange);
            }
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
     * @param hClass the h class
     * @param method the method
     * @return the path matcher
     */
    private PathMatcher getPathMatcher(final Class<? extends FlowHandler> hClass, final Method method, final MethodType type) {
        final PathPrefix pathPrefix = hClass.getAnnotation(PathPrefix.class);
        final boolean forceLazy = (type == MethodType.HANDLER);

        if (method.isAnnotationPresent(Path.class)) {
            final Path path = method.getAnnotation(Path.class);
            return new PathMatcher(this.exchange.getRelativePath(), pathPrefix, path, forceLazy);
        } else if (method.isAnnotationPresent(Paths.class)) {
            final Paths paths = method.getAnnotation(Paths.class);
            for (final Path path : paths.value()) {
                final PathMatcher pathMatcher = new PathMatcher(this.exchange.getRelativePath(), pathPrefix, path, forceLazy);
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
     * Method has body boolean.
     *
     * @return the boolean
     */
    private boolean methodHasBody() {
        final Optional<POST> post = Optional.ofNullable(this.method.getAnnotation(POST.class));
        final Optional<PATCH> patch = Optional.ofNullable(this.method.getAnnotation(PATCH.class));
        final Optional<PUT> put = Optional.ofNullable(this.method.getAnnotation(PUT.class));

        return post.isPresent() || patch.isPresent() || put.isPresent();
    }

    /**
     * Gets form data.
     *
     * @param exchange the exchange
     * @return the form data
     */
    private FormData getFormData(final HttpServerExchange exchange) {
        final FormDataParser p = FormParserFactory.builder(true).build().createParser(exchange);

        if (p == null) {
            return null;
        }
        p.setCharacterEncoding("UTF-8");

        try {
            return p.parseBlocking();
        } catch (final IOException e) {
            this.logger.error("Error while parsing form data.", e);
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
            this.controllerInjectable.put(value.getClass(), () -> value);
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
        this.controllerInjectable.put(tClass, () -> value);
    }

    /**
     * Add injectable.
     *
     * @param tClass the t class
     * @param value  the value
     */
    public void addInjectableUnsafe(final Class<?> tClass, final Object value) {
        this.controllerInjectable.put(tClass, () -> value);
    }
}
