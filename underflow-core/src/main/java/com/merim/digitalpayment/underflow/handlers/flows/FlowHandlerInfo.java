package com.merim.digitalpayment.underflow.handlers.flows;

import jakarta.ws.rs.Path;
import lombok.Getter;

/**
 * FlowHandlerInfo.
 *
 * @author Pierre Adam
 * @since 24.05.30
 */
@Getter
public class FlowHandlerInfo {

    /**
     * The Handler class.
     */
    private final Class<? extends FlowHandler> handlerClass;

    /**
     * The Non variable path.
     */
    private final String nonVariablePath;

    /**
     * Instantiates a new Flow handler info.
     *
     * @param handlerClass the handler class
     * @param instance     the instance
     */
    private FlowHandlerInfo(final Class<? extends FlowHandler> handlerClass, final FlowHandler instance) {
        this.handlerClass = handlerClass;

        if (!handlerClass.isAnnotationPresent(Path.class)) {
            throw new RuntimeException("Class " + handlerClass.getCanonicalName() + " is not annotated with JAX-RS @Path.");
        }

        this.nonVariablePath = handlerClass.getAnnotation(Path.class).value();
    }

    /**
     * Create flow handler info.
     *
     * @param hClass   the h class
     * @param instance the instance
     * @return the flow handler info
     */
    public static FlowHandlerInfo create(final Class<? extends FlowHandler> hClass, final FlowHandler instance) {
        // TODO : Check type of instance.
        return new FlowHandlerInfo(hClass, instance);
    }
}
