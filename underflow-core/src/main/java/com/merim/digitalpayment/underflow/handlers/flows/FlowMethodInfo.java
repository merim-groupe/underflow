package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.enums.MethodType;
import com.merim.digitalpayment.underflow.handlers.flows.exceptions.InvalidMethodException;
import com.merim.digitalpayment.underflow.routing.RouteResolver;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * FlowMethodInfo.
 *
 * @author Pierre Adam
 * @since 24.05.30
 */
@Slf4j
@Getter
public class FlowMethodInfo {

    /**
     * The Handler info.
     */
    private final FlowHandlerInfo handlerInfo;

    /**
     * The Class method.
     */
    private final Method method;

    /**
     * The Method type.
     */
    private final MethodType methodType;

    /**
     * The Http method.
     */
    private final String httpMethod;

    /**
     * The Path pattern.
     */
    private final RouteResolver route;

    /**
     * Instantiates a new Flow method info.
     *
     * @param handlerInfo the handler info
     * @param method      the class method
     * @throws InvalidMethodException the invalid method exception
     */
    public FlowMethodInfo(final FlowHandlerInfo handlerInfo,
                          final Method method) throws InvalidMethodException {
        this.handlerInfo = handlerInfo;
        this.method = method;
        this.methodType = MethodType.resolve(method);
        this.httpMethod = FlowMethodInfo.extractHttpMethod(this.method);
        this.route = new RouteResolver(handlerInfo.getHandlerClass(), method, false);
        this.checkMethod();
    }

    /**
     * Extract http method string.
     *
     * @param method the method
     * @return the string
     * @throws InvalidMethodException the invalid method exception
     */
    private static String extractHttpMethod(final Method method) throws InvalidMethodException {
        HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);

        if (httpMethod == null) {
            for (final Annotation annotation : method.getAnnotations()) {
                httpMethod = annotation.annotationType().getAnnotation(HttpMethod.class);
                if (httpMethod != null) {
                    break;
                }
            }
        }

        if (httpMethod == null) {
            throw new InvalidMethodException();
        }

        return httpMethod.value();
    }

    /**
     * Check method.
     */
    private void checkMethod() {
        Arrays.stream(this.method.getParameters())
                .filter(parameter ->
                        !parameter.isAnnotationPresent(Context.class) &&
                                !parameter.isAnnotationPresent(QueryParam.class) &&
                                !parameter.isAnnotationPresent(PathParam.class)
                )
                .forEach(parameter -> LoggerFactory
                        .getLogger(this.method.getDeclaringClass())
                        .warn("Unable to resolve the argument <{}@{}> for the method {}.{}. " +
                                        "Please use the annotation @PathParam, @QueryParam, @Context to specify how to resolve this argument.",
                                parameter.getName(), parameter.getType().getSimpleName(),
                                this.method.getDeclaringClass().getSimpleName(), this.method.getName()));
    }
}
