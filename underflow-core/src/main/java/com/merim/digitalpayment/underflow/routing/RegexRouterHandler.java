package com.merim.digitalpayment.underflow.routing;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.ResponseCodeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MainRouterHandler.
 *
 * @author Pierre Adam
 * @since 24.06.06
 */
public class RegexRouterHandler implements HttpHandler {

    /**
     * The Handlers.
     */
    private final Map<Pattern, HttpHandler> handlers;

    /**
     * Instantiates a new Regex router handler.
     */
    public RegexRouterHandler() {
        this.handlers = new HashMap<>();
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        final String relativePath = exchange.getRelativePath();
        String matchedPart = null;
        String nonMatchedPart = null;
        HttpHandler matchedHandler = null;

        for (final Pattern pattern : this.handlers.keySet()) {
            final Matcher matcher = pattern.matcher(relativePath);
            if (matcher.find() && (matchedPart == null || matchedPart.length() < matcher.group().length())) {
                matchedPart = matcher.group();
                nonMatchedPart = relativePath.replace(matchedPart, "");
                matchedHandler = this.handlers.get(pattern);
            }
        }

        if (matchedHandler == null) {
            ResponseCodeHandler.HANDLE_404.handleRequest(exchange);
            return;
        }

        exchange.setRelativePath(nonMatchedPart);

        if (exchange.getResolvedPath().isEmpty()) {
            exchange.setResolvedPath(matchedPart);
        } else {
            exchange.setResolvedPath(exchange.getResolvedPath() + matchedPart);
        }

        exchange.setRelativePath(nonMatchedPart);

        matchedHandler.handleRequest(exchange);
    }

    /**
     * Add prefix path regex router handler.
     *
     * @param prefix  the prefix
     * @param handler the handler
     * @return the regex router handler
     */
    public synchronized RegexRouterHandler addPrefixPath(final String prefix, final HttpHandler handler) {
        final String regexPrefix = (prefix.startsWith("^") ? prefix : "^" + prefix).replaceAll("[/]+$", "");

        this.handlers.put(Pattern.compile(regexPrefix), handler);

        return this;
    }
}
