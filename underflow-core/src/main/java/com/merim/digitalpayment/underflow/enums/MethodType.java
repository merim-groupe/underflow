package com.merim.digitalpayment.underflow.enums;

import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
import com.merim.digitalpayment.underflow.results.Result;
import org.wildfly.common.annotation.NotNull;

import java.lang.reflect.Method;

/**
 * MethodType.
 *
 * @author Pierre Adam
 * @since 23.04.13
 */
public enum MethodType {

    /**
     * Unsupported method type.
     */
    UNSUPPORTED(null),

    /**
     * Result method type.
     */
    RESULT(Result.class),

    /**
     * Handler method type.
     */
    HANDLER(FlowHandler.class);

    /**
     * The Expected return type.
     */
    private final Class<?> expectedReturnType;

    /**
     * Instantiates a new Method type.
     *
     * @param expectedReturnType the expected return type
     */
    MethodType(final Class<?> expectedReturnType) {
        this.expectedReturnType = expectedReturnType;
    }

    /**
     * Resolve method type.
     *
     * @param method the method
     * @return the method type
     */
    @NotNull
    public static MethodType resolve(@NotNull final Method method) {
        return MethodType.resolve(method.getReturnType());
    }

    /**
     * Resolve method type.
     *
     * @param type the type
     * @return the method type
     */
    @NotNull
    public static MethodType resolve(@NotNull final Class<?> type) {
        for (final MethodType value : MethodType.values()) {
            if (value.expectedReturnType == null) {
                continue;
            }

            if (type.isAssignableFrom(value.expectedReturnType)) {
                return value;
            }
        }

        return MethodType.UNSUPPORTED;
    }
}
