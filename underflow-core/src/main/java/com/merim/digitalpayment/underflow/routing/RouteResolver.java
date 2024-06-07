package com.merim.digitalpayment.underflow.routing;

import com.merim.digitalpayment.underflow.annotation.routing.Converter;
import com.merim.digitalpayment.underflow.converters.Converters;
import com.merim.digitalpayment.underflow.handlers.flows.FlowHandler;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * RouteResolver.
 *
 * @author Pierre Adam
 * @since 24.06.03
 */
@Slf4j
public class RouteResolver {

    /**
     * The constant routeArgumentPattern.
     */
    public static final Pattern ROUTE_ARGUMENT_PATTERN = Pattern.compile("\\{(?<name>[\\w\\-_]+)(:(?<pattern>[^:]+))?}");

    /**
     * The Raw route.
     */
    @Getter
    private final String rawRoute;

    /**
     * The Pattern.
     */
    private final Pattern pattern;

    /**
     * Resolve route.
     *
     * @param route      the route
     * @param method     the method
     * @param ignoreCase the ignore case
     */
    public RouteResolver(final String route, final Method method, final boolean ignoreCase) {
        this.rawRoute = route;
        this.pattern = RouteResolver.routeToRegex(route, method, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);
    }

    /**
     * Route to regex pattern.
     *
     * @param hClass     the h class
     * @param method     the method
     * @param ignoreCase the ignore case
     */
    public RouteResolver(final Class<? extends FlowHandler> hClass, final Method method, final boolean ignoreCase) {
        this(RouteResolver.extractPath(hClass, method), method, ignoreCase);
    }

    /**
     * Route to regex pattern.
     *
     * @param route  the route
     * @param method the method
     * @param flag   the flag
     * @return the pattern
     */
    private static Pattern routeToRegex(final String route,
                                        final Method method,
                                        final int flag) {
        return Pattern.compile(RouteResolver.compileRoute(route, method), flag);
    }

    /**
     * Compile route string.
     *
     * @param route  the route
     * @param method the method
     * @return the string
     */
    private static String compileRoute(final String route,
                                       final Method method) {
        return RouteResolver.compileRoute(route, name -> RouteResolver.extractPattern(method, name));
    }

    /**
     * Compile route string.
     *
     * @param route            the route
     * @param patternExtractor the pattern extractor
     * @return the string
     */
    public static String compileRoute(final String route,
                                      final Function<String, String> patternExtractor) {
        final StringBuffer compiledRoute = new StringBuffer();
        final Matcher matcher = RouteResolver.ROUTE_ARGUMENT_PATTERN.matcher(route);

        while (matcher.find()) {
            final String name = matcher.group("name");
            String pattern = matcher.group("pattern");

            if (pattern == null) {
                pattern = patternExtractor.apply(name);
            }

            // Do not use String.format here because it will break the escapement.
            final String replacement = ("(?<" + name + ">" + pattern + ")").replaceAll("\\\\", "\\\\\\\\");

            matcher.appendReplacement(compiledRoute, replacement);
        }

        matcher.appendTail(compiledRoute);

        return compiledRoute.toString();
    }

    /**
     * Gets non variable path.
     *
     * @param basePath the base path
     * @return the non variable path
     */
    public static String getNonVariablePath(final String basePath) {
        final Matcher pathMatcher = RouteResolver.ROUTE_ARGUMENT_PATTERN.matcher(basePath);

        if (pathMatcher.find()) {
            int idx;

            for (idx = pathMatcher.start(); idx > 0; idx--) {
                if (basePath.charAt(idx) == '/') {
                    break;
                }
            }

            if (idx == 0) {
                return "/";
            }

            return basePath.substring(0, idx);
        }

        return basePath;
    }

    /**
     * Gets variable path.
     *
     * @param basePath the base path
     * @return the variable path
     */
    public static String getVariablePath(final String basePath) {
        final Matcher pathMatcher = RouteResolver.ROUTE_ARGUMENT_PATTERN.matcher(basePath);

        if (pathMatcher.find()) {
            int idx;

            for (idx = pathMatcher.start(); idx > 0; idx--) {
                if (basePath.charAt(idx) == '/') {
                    break;
                }
            }

            if (idx == 0) {
                return basePath;
            }

            return basePath.substring(idx);
        }

        return "";
    }

    /**
     * Extract pattern string.
     *
     * @param method the method
     * @param name   the name
     * @return the string
     */
    private static String extractPattern(final Method method,
                                         final String name) {
        Parameter parameter = null;
        for (final Parameter param : method.getParameters()) {
            if (param.isAnnotationPresent(PathParam.class)) {
                final PathParam pathParam = param.getAnnotation(PathParam.class);
                if (pathParam.value().equals(name)) {
                    parameter = param;
                }
            }
        }

        if (parameter != null) {
            final Converter pConverter = parameter.getAnnotation(Converter.class);
            final Class<?> paramType = parameter.getType();

            try {
                if (pConverter != null) {
                    return Converters.getRuntimeConverter(pConverter.value()).getSyntax();
                }
                return Converters.getSyntax(paramType);
            } catch (final IllegalArgumentException e) {
                RouteResolver.logger.warn("Unable to automatically resolve parameter {} of type {} from method {}.{}.",
                        name, paramType, method.getDeclaringClass().getName(), method.getName());
                return "[^\\\\]+";
            }
        } else {
            RouteResolver.logger.warn("Unable to resolve parameter {} from method {}.{}", name, method.getDeclaringClass().getName(), method.getName());
            return "[^\\\\]+";
        }
    }

    /**
     * Gets path regex.
     *
     * @param hClass the h class
     * @param method the method
     * @return the path regex
     */
    private static String extractPath(final Class<? extends FlowHandler> hClass, final Method method) {
        String path = hClass.getAnnotation(Path.class).value().replaceAll("(/)+$", "");

        if (method.isAnnotationPresent(Path.class)) {
            final String methodPath = method.getAnnotation(Path.class).value();

            path = (path.isEmpty() ? "" : path + "/") + methodPath
                    .replaceAll("^(/)+", "")
                    .replaceAll("(/)+$", "");
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.endsWith("/")) {
            path = path.replaceAll("(/)+$", "");
        }

        if (path.isEmpty()) {
            path = "/";
        }

        return "^" + path + "$";
    }

    /**
     * Gets matcher for.
     *
     * @param path the path
     * @return the matcher for
     */
    public Matcher getMatcherFor(final String path) {
        return this.pattern.matcher(path);
    }
}
