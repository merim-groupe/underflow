package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.routing.RouteResolver;
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
     * The Instance.
     */
    private final FlowHandler instance;

    /**
     * The Base path.
     */
    private final String basePath;

    /**
     * The Non variable path.
     */
    private final String nonVariablePath;

    /**
     * The Regex path.
     */
    private final String regexPath;

    /**
     * The Variable regex path.
     */
    private final String variableRegexPath;

    /**
     * Instantiates a new Flow handler info.
     *
     * @param handlerClass the handler class
     * @param instance     the instance
     */
    private FlowHandlerInfo(final Class<? extends FlowHandler> handlerClass, final FlowHandler instance) {
        this.handlerClass = handlerClass;
        this.instance = instance;

        if (!handlerClass.isAnnotationPresent(Path.class)) {
            throw new RuntimeException("Class " + handlerClass.getCanonicalName() + " is not annotated with JAX-RS @Path.");
        }

        final String path = handlerClass.getAnnotation(Path.class).value();

        this.basePath = path.startsWith("/") ? path : "/" + path;
        this.nonVariablePath = RouteResolver.getNonVariablePath(this.basePath);
        this.variableRegexPath = RouteResolver.compileRoute(RouteResolver.getVariablePath(this.basePath), s -> "[^/]+");
        this.regexPath = RouteResolver.compileRoute(this.basePath, s -> "[^/]+");
    }

    /**
     * Create flow handler info.
     *
     * @param hClass   the h class
     * @param instance the instance
     * @return the flow handler info
     */
    public static FlowHandlerInfo create(final Class<? extends FlowHandler> hClass, final FlowHandler instance) {
        return new FlowHandlerInfo(hClass, instance);
    }
}
