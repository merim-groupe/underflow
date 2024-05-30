package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.enums.MethodType;
import com.merim.digitalpayment.underflow.handlers.flows.exceptions.InvalidMethodException;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * FlowMethodInfo.
 *
 * @author Pierre Adam
 * @since 24.05.30
 */
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
    private final Pattern pathPattern;

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
        this.pathPattern = Pattern.compile(FlowMethodInfo.getPathRegex(handlerInfo.getHandlerClass(), method), 0);
        // TODO : QUERY STRING Handling here directly.
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
     * Gets path regex.
     *
     * @param hClass the h class
     * @param method the method
     * @return the path regex
     */
    private static String getPathRegex(final Class<? extends FlowHandler> hClass, final Method method) {
        String pathRegex = hClass.getAnnotation(Path.class).value().replaceAll("(/)+$", "");

        if (method.isAnnotationPresent(Path.class)) {
            final String methodPath = method.getAnnotation(Path.class).value();
            pathRegex = (pathRegex.isEmpty() ? "" : pathRegex + "/") + methodPath.replaceAll("(/)+$", "");
        }

        if (!pathRegex.startsWith("/")) {
            pathRegex = "/" + pathRegex;
        }

        if (pathRegex.endsWith("/")) {
            pathRegex = pathRegex.replaceAll("(/)+$", "");
        }

        if (pathRegex.isEmpty()) {
            pathRegex = "/";
        }

        return "^" + pathRegex + "$";
    }
}
