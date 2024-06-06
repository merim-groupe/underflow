package com.merim.digitalpayment.underflow.handlers.context;

import com.merim.digitalpayment.underflow.annotation.routing.Converter;
import com.merim.digitalpayment.underflow.annotation.routing.QueryParamList;
import com.merim.digitalpayment.underflow.app.Application;
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
     * The Controller injectable.
     */
    private final Map<Class<?>, Supplier<Object>> controllerInjectable;

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
        this.methodType = MethodType.UNSUPPORTED;
        this.method = null;
        this.pathMatcher = null;
        this.queryString = null;
        this.controllerInjectable = new HashMap<>();
        this.controllerInjectable.put(FormData.class, () -> this.getFormData(this.exchange));
        this.controllerInjectable.put(HttpServerExchange.class, () -> this.exchange);
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
        return Optional.ofNullable(this.method.getAnnotation(Secured.class));
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
                this.controllerInjectable.put(InputStream.class, this.exchange::getInputStream);
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
            final Context pContext = parameter.getAnnotation(Context.class);
            final Converter pConverter = parameter.getAnnotation(Converter.class);
            final PathParam pPathParam = parameter.getAnnotation(PathParam.class);
            final QueryParam pQueryParam = parameter.getAnnotation(QueryParam.class);
//            final DefaultValue pDefaultValue = parameter.getAnnotation(DefaultValue.class);
            final Optional<?> appInject = Application.getInstanceOptional(pClass);

            if (pPathParam != null) {
                if (this.pathMatcherHasGroup(pPathParam.value())) {
                    final String value = this.pathMatcherGetGroup(pPathParam.value());
                    methodArgs.add(this.queryConvert(pConverter, pClass, value));
                } else {
                    this.handlerLogger.warn("A @Named(\"{}\") argument was requested for the method {}.{}. " +
                                    "This argument was not found on the path. Please check your @Path syntaxes.",
                            pPathParam.value(), this.handler.getClass().getSimpleName(), this.method.getName());
                    methodArgs.add(null);
                }
            } else if (pQueryParam != null) {
                final QueryParamList pQueryParamList = parameter.getAnnotation(QueryParamList.class);
                final Deque<String> values = this.queryString.getValuesFor(pQueryParam.value());

                if (pQueryParamList != null) {
                    methodArgs.add(values.stream()
                            .map(v -> this.queryConvert(pConverter, pQueryParamList.value(), v))
                            .collect(Collectors.toList()));
                } else {
                    if (values.isEmpty()) {
                        methodArgs.add(null);
                    } else {
                        methodArgs.add(this.queryConvert(pConverter, pClass, values.getFirst()));
                    }
                }
            } else if (pContext != null) {
                if (this.controllerInjectable.containsKey(pClass)) {
                    methodArgs.add(this.controllerInjectable.get(pClass).get());
                } else if (appInject.isPresent()) {
                    methodArgs.add(appInject.get());
                } else {
                    this.handlerLogger.warn("Unable to resolve the argument <{}@{}> for the method {}.{}. " +
                                    "Please register the corresponding class with Application.register().",
                            parameter.getName(), pClass.getSimpleName(), this.handler.getClass().getSimpleName(), this.method.getName());
                    methodArgs.add(null);
                }
            } else {
                // TODO : Allow for automatic form resolution here.
                this.handlerLogger.warn("Unable to resolve the argument <{}@{}> for the method {}.{}. " +
                                "Please use the annotation @PathParam, @QueryParam, @Context to specify how to resolve this argument.",
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
        try {
            if (this.methodType == MethodType.RESULT) {
                final Result result = (Result) this.method.invoke(this.handler, methodArgs.toArray());
                result.process(this.exchange, this.method);
            } else if (this.methodType == MethodType.STRING) {
                final SimpleStringResult result = new SimpleStringResult((String) this.method.invoke(this.handler, methodArgs.toArray()));
                result.process(this.exchange, this.method);
            }
        } catch (final Throwable e) {
            Throwable cause = e;
            if (e instanceof InvocationTargetException) {
                cause = e.getCause();
            }
            ContextHandler.logger.error("Controller uncaught exception.", cause);
            try {
                this.handler.onException(cause).process(this.exchange, null);
            } catch (final Exception e2) {
                throw new RuntimeException(e2);
            }
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
