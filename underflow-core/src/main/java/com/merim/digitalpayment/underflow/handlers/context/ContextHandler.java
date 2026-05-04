package com.merim.digitalpayment.underflow.handlers.context;

import com.merim.digitalpayment.underflow.annotation.AnnotationResolver;
import com.merim.digitalpayment.underflow.annotation.routing.Converter;
import com.merim.digitalpayment.underflow.annotation.routing.QueryParamList;
import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.attachments.UnderflowKeys;
import com.merim.digitalpayment.underflow.converters.Converters;
import com.merim.digitalpayment.underflow.converters.IConverter;
import com.merim.digitalpayment.underflow.enums.MethodType;
import com.merim.digitalpayment.underflow.handlers.context.path.QueryString;
import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
import com.merim.digitalpayment.underflow.handlers.flows.FlowMethodInfo;
import com.merim.digitalpayment.underflow.mdc.MDCContext;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.results.SimpleStringResult;
import com.merim.digitalpayment.underflow.security.annotations.Secured;
import io.undertow.server.BlockingHttpExchange;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * HandlerContext.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
@Slf4j
public class ContextHandler implements MDCContext {

    /**
     * The Handler logger.
     */
    private final Logger handlerLogger;

    /**
     * The Handler.
     */
    private final FlowHandler handler;

    /**
     * The Exchange.
     */
    private final HttpServerExchange exchange;


    /**
     * The Worker executor.
     */
    private final ExecutorService workerExecutor;

    /**
     * The Response executor.
     */
    private final ExecutorService responseExecutor;

    /**
     * The After response executor.
     */
    private final ExecutorService afterResponseExecutor;

    /**
     * The Controller injectable.
     */
    private final Map<Class<?>, Function<HttpServerExchange, ?>> controllerInjectable;

    /**
     * The Method type.
     */
    private MethodType methodType;

    /**
     * The Method.
     */
    @Getter
    private Method method;

    /**
     * The Matcher.
     */
    private Matcher pathMatcher;

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
        this.handlerLogger = LoggerFactory.getLogger(handler.getClass());
        this.handler = handler;
        this.exchange = exchange;
        final ExecutorService worker = exchange.getAttachment(UnderflowKeys.WORKER_EXECUTOR_KEY);
        this.workerExecutor = worker != null ? worker : ForkJoinPool.commonPool();
        final ExecutorService response = exchange.getAttachment(UnderflowKeys.RESPONSE_EXECUTOR_KEY);
        this.responseExecutor = response != null ? response : ForkJoinPool.commonPool();
        final ExecutorService afterResponse = exchange.getAttachment(UnderflowKeys.AFTER_RESPONSE_EXECUTOR_KEY);
        this.afterResponseExecutor = afterResponse != null ? afterResponse : ForkJoinPool.commonPool();
        this.methodType = MethodType.UNSUPPORTED;
        this.method = null;
        this.pathMatcher = null;
        this.queryString = null;
        this.controllerInjectable = new HashMap<>();
        this.controllerInjectable.put(FormData.class, this::getFormData);
        this.controllerInjectable.put(HttpServerExchange.class, (e) -> e);
    }

    /**
     * Check that there is a valid method to handle the call.
     *
     * @return true if there is a valid method
     */
    public boolean isValid() {
        final String httpMethod = this.exchange.getRequestMethod().toString();

        for (final FlowMethodInfo methodInfo : this.handler.getMethodsInfo()) {
            if (methodInfo.getMethodType() != MethodType.UNSUPPORTED) {
                if (Objects.equals(methodInfo.getHttpMethod(), httpMethod)) {
                    final Matcher matcher = methodInfo.getRoute().getMatcherFor(this.exchange.getRequestPath());
                    if (matcher.find()) {
                        final QueryString parameter = new QueryString(this.exchange.getQueryParameters(), methodInfo.getMethod());
                        if (parameter.checkRequired()) {
                            if (this.method == null) {
                                this.method = methodInfo.getMethod();
                                this.methodType = methodInfo.getMethodType();
                                this.pathMatcher = matcher;
                                this.queryString = parameter;
                            }
                        }
                    }
                }
            } else {
                ContextHandler.logger.error("Method return type is not supported: {}", methodInfo.getMethod());
            }
        }

        return this.method != null;
    }

    /**
     * Require security.
     *
     * @return the boolean
     */
    public Optional<Secured> requireSecurity() {
        return AnnotationResolver.annotation(this.method, Secured.class);
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
                try (final BlockingHttpExchange blocking = this.exchange.startBlocking()) {
                    this.run();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                this.run();
            }
        }
    }

    /**
     * Resolve the arguments and execute de method.
     */
    private void run() {
        try {
            if (this.methodHasBody()) {
                this.controllerInjectable.put(InputStream.class, HttpServerExchange::getInputStream);
            }

            final List<Object> methodArgs = this.resolveMethodArgs();

            this.safeHttpExecute(() -> this.executeMethod(methodArgs));
        } catch (final Exception e) {
            ContextHandler.logger.error("An error occurred.", e);

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
            final Optional<Context> oContext = AnnotationResolver.nestedAnnotation(parameter, Context.class);
            final Optional<Converter> oConverter = AnnotationResolver.nestedAnnotation(parameter, Converter.class);
            final Optional<PathParam> oPathParam = AnnotationResolver.nestedAnnotation(parameter, PathParam.class);
            final Optional<QueryParam> oQueryParam = AnnotationResolver.nestedAnnotation(parameter, QueryParam.class);
            final Optional<CookieParam> oCookieParam = AnnotationResolver.nestedAnnotation(parameter, CookieParam.class);
            final Optional<HeaderParam> oHeaderParam = AnnotationResolver.nestedAnnotation(parameter, HeaderParam.class);
            final Optional<?> appInject = Application.getInstanceOptional(pClass);

            if (oPathParam.isPresent()) {
                final String pathParamValue = oPathParam.get().value();
                if (this.pathMatcherHasGroup(pathParamValue)) {
                    final String value = this.pathMatcherGetGroup(pathParamValue);
                    methodArgs.add(this.queryConvert(oConverter.orElse(null), pClass, value));
                } else {
                    this.handlerLogger.warn("A @Named(\"{}\") argument was requested for the method {}.{}. " +
                                    "This argument was not found on the path. Please check your @Path syntaxes.",
                            pathParamValue, this.handler.getClass().getSimpleName(), this.method.getName());
                    methodArgs.add(null);
                }
            } else if (oQueryParam.isPresent()) {
                final Optional<QueryParamList> oQueryParamList = AnnotationResolver.nestedAnnotation(parameter, QueryParamList.class);
                final Deque<String> values = this.queryString.getValuesFor(oQueryParam.get().value());

                if (oQueryParamList.isPresent()) {
                    methodArgs.add(values.stream()
                            .map(v -> this.queryConvert(oConverter.orElse(null), oQueryParamList.get().value(), v))
                            .collect(Collectors.toList()));
                } else {
                    if (values.isEmpty()) {
                        methodArgs.add(null);
                    } else {
                        methodArgs.add(this.queryConvert(oConverter.orElse(null), pClass, values.getFirst()));
                    }
                }
            } else if (oContext.isPresent()) {
                if (this.controllerInjectable.containsKey(pClass)) {
                    methodArgs.add(this.controllerInjectable.get(pClass).apply(this.exchange));
                } else if (appInject.isPresent()) {
                    methodArgs.add(appInject.get());
                } else {
                    this.handlerLogger.warn("Unable to resolve the argument <{}@{}> for the method {}.{}. " +
                                    "Please register the corresponding class with Application.register().",
                            parameter.getName(), pClass.getSimpleName(), this.handler.getClass().getSimpleName(), this.method.getName());
                    methodArgs.add(null);
                }
            } else if (oCookieParam.isPresent()) {
                final Cookie requestCookie = this.exchange.getRequestCookie(oCookieParam.get().value());

                if (requestCookie == null) {
                    methodArgs.add(this.queryConvert(oConverter.orElse(null), pClass, null));
                } else if (pClass.isAssignableFrom(Cookie.class)) {
                    methodArgs.add(requestCookie);
                } else {
                    methodArgs.add(this.queryConvert(oConverter.orElse(null), pClass, requestCookie.getValue()));
                }
            } else if (oHeaderParam.isPresent()) {
                final String headerValue = this.exchange.getRequestHeaders().getFirst(oHeaderParam.get().value());
                if (headerValue == null) {
                    methodArgs.add(this.queryConvert(oConverter.orElse(null), pClass, null));
                } else {
                    methodArgs.add(this.queryConvert(oConverter.orElse(null), pClass, headerValue));
                }
            } else {
                // TODO : Allow for automatic form resolution here.
                this.handlerLogger.warn("Unable to resolve the argument <{}@{}> for the method {}.{}. " +
                                "Please use the annotation @PathParam, @QueryParam, @CookieParam, @HeaderParam, @Context to specify how to resolve this argument.",
                        parameter.getName(), pClass.getSimpleName(), this.handler.getClass().getSimpleName(), this.method.getName());
                methodArgs.add(null);
            }
        }

        return methodArgs;
    }

    /**
     * Path matcher has group boolean.
     *
     * @param groupName the group name
     * @return the boolean
     */
    private boolean pathMatcherHasGroup(final String groupName) {
        try {
            final String group = this.pathMatcher.group(groupName);
            return group != null;
        } catch (final Exception ignore) {
            return false;
        }
    }

    /**
     * Path matcher get group string.
     *
     * @param groupName the group name
     * @return the string
     */
    private String pathMatcherGetGroup(final String groupName) {
        return this.pathMatcher.group(groupName);
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
        if (queryConverter != null) {
            final IConverter<?> converter = Converters.getRuntimeConverter(queryConverter.value());

            if (!pClass.isAssignableFrom(converter.getBackedType())) {
                ContextHandler.logger.error("Invalid converter. Converter {} is able to handle {} but {} was given as argument.",
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
            ContextHandler.logger.error("Something wrong happened.", e);
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
        final CompletableFuture<Result> completableFuture = this.toCompletableFuture(methodArgs);
        completableFuture.whenCompleteAsync((result, throwable) -> {
            if (throwable != null) {
                Throwable cause = throwable;
                if (cause instanceof final CompletionException e) {
                    cause = e.getCause();
                }

                if (cause instanceof final EncapsulatedException e) {
                    cause = e.getCause();
                }

                if (cause instanceof final InvocationTargetException e) {
                    cause = e.getCause();
                }

                ContextHandler.logger.error("Controller uncaught exception.", cause);
                try {
                    this.handler.onException(cause).process(this.exchange, null);
                } catch (final Exception e) {
                    ContextHandler.logger.error("Unable to send error response.", e);
                }
            } else {
                this.safeHttpExecute(() -> result.process(this.exchange, this.method));

                result.andThen()
                        .ifPresent(runnable -> CompletableFuture
                                .runAsync(runnable, this.afterResponseExecutor)
                                .exceptionally(throwable1 -> {
                                    Throwable cause = throwable1;
                                    if (cause instanceof final CompletionException e) {
                                        cause = e.getCause();
                                    }

                                    ContextHandler.logger.error("Error while running andThen() from {}", result.getClass().getSimpleName(), cause);
                                    return null;
                                }));
            }
        }, this.responseExecutor);

        // Possible improvement, adding handling for the closure of the socket on the client side.
        // Could be implemented by having a monitoring on the connection. Example:
        /*
        ScheduledFuture<?> monitor = connectionMonitor.scheduleAtFixedRate(() -> {
            if (!this.exchange.getConnection().isOpen()) {
                ContextHandler.logger.info("Connection closed detected by monitor");
                future.cancel(true);
            }
        }, 0, 500, TimeUnit.MILLISECONDS); // Check every 500ms
        */
    }

    /**
     * To completable future.
     *
     * @param methodArgs the method args
     * @return the completable future
     */
    private CompletableFuture<Result> toCompletableFuture(final List<Object> methodArgs) {
        if (this.methodType == MethodType.RESULT) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return (Result) this.method.invoke(this.handler, methodArgs.toArray());
                } catch (final IllegalAccessException | InvocationTargetException e) {
                    throw new EncapsulatedException(e);
                }
            }, this.workerExecutor);
        } else if (this.methodType == MethodType.STRING) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return new SimpleStringResult((String) this.method.invoke(this.handler, methodArgs.toArray()));
                } catch (final IllegalAccessException | InvocationTargetException e) {
                    throw new EncapsulatedException(e);
                }
            }, this.workerExecutor);
        } else if (this.methodType == MethodType.COMPLETABLE_FUTURE) {
            return CompletableFuture.supplyAsync(() -> {
                        try {
                            return (CompletableFuture<?>) this.method.invoke(this.handler, methodArgs.toArray());
                        } catch (final IllegalAccessException | InvocationTargetException e) {
                            throw new EncapsulatedException(e);
                        }
                    }, this.workerExecutor)
                    .thenComposeAsync(future -> future, this.responseExecutor)
                    .thenApply(o -> {
                        if (o instanceof final String stringResult) {
                            return new SimpleStringResult(stringResult);
                        } else if (o instanceof final Result result) {
                            return result;
                        } else {
                            throw new RuntimeException("Unsupported return type. Please return a String or a Result");
                        }
                    });
        } else {
            throw new RuntimeException("Unsupported return type. Please return a String, a Result, a CompletableFuture<String> or a CompletableFuture<Result>");
        }
    }

    /**
     * Method has body boolean.
     *
     * @return the boolean
     */
    private boolean methodHasBody() {
        return this.method.isAnnotationPresent(POST.class) ||
                this.method.isAnnotationPresent(PATCH.class) ||
                this.method.isAnnotationPresent(PUT.class);
    }

    /**
     * Gets form data.
     *
     * @param exchange the exchange
     * @return the form data
     */
    private FormData getFormData(final HttpServerExchange exchange) {
        try (final FormDataParser p = FormParserFactory.builder(true)
                .build()
                .createParser(exchange)) {

            if (p == null) {
                return null;
            }
            p.setCharacterEncoding("UTF-8");

            try {
                return p.parseBlocking();
            } catch (final IOException e) {
                ContextHandler.logger.error("Error while parsing form data.", e);
                return null;
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Add injectable.
     *
     * @param <T>      the type parameter
     * @param tClass   the t class
     * @param supplier the supplier
     */
    public <T> void addInjectable(final Class<T> tClass, final Function<HttpServerExchange, T> supplier) {
        if (supplier != null) {
            this.controllerInjectable.put(tClass, supplier);
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
            this.controllerInjectable.put(value.getClass(), (e) -> value);
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
        this.controllerInjectable.put(tClass, (e) -> value);
    }

    /**
     * Add injectable.
     *
     * @param tClass the t class
     * @param value  the value
     */
    public void addInjectableUnsafe(final Class<?> tClass, final Object value) {
        this.controllerInjectable.put(tClass, (e) -> value);
    }

    /**
     * The type Encapsulated exception.
     */
    public static class EncapsulatedException extends RuntimeException {
        /**
         * Instantiates a new Encapsulated exception.
         *
         * @param cause the cause
         */
        public EncapsulatedException(final Throwable cause) {
            super(cause);
        }
    }
}
