package com.merimdigitalmedia.underflow.mdc;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * MDCServer.
 *
 * @author Pierre Adam
 * @since 21.09.28
 */
public interface MDCContext {

    /**
     * Intercept request.
     *
     * @param exchange the exchange
     */
    default void addMDCServerContext(final HttpServerExchange exchange) {
        if (MDC.getMDCAdapter() != null && !this.getMDC(MDCKeys.Connection.IO_THREAD).isPresent()) {
            final ServerConnection connection = exchange.getConnection();

            this.putMDC(MDCKeys.Connection.IO_THREAD, connection.getIoThread().toString());
            this.putMDC(MDCKeys.Connection.PEER_ADDRESS, connection.getPeerAddress().toString());
            this.putMDC(MDCKeys.Request.UID, UUID.randomUUID().toString());
            this.putMDC(MDCKeys.Request.METHOD, exchange.getRequestMethod().toString());
            this.putMDC(MDCKeys.Request.URL, exchange.getRequestURL());
            this.putMDC(MDCKeys.Request.QUERY_STRING, exchange.getQueryString());
            this.putMDC(MDCKeys.Request.HOST_NAME, exchange.getHostName());
            this.putMDC(MDCKeys.Request.HOST_PORT, String.format("%d", exchange.getHostPort()));
        }
    }

    /**
     * Extract and clear mdc map.
     *
     * @return the map
     */
    default Map<String, String> popMDCContext() {
        final Map<String, String> mdcContext;

        if (MDC.getMDCAdapter() != null) {
            mdcContext = MDC.getCopyOfContextMap();
            MDC.clear();
        } else {
            mdcContext = null;
        }

        return mdcContext;
    }

    /**
     * With mdc context.
     *
     * @param mdcContext the mdc context
     * @param runnable   the runnable
     */
    default void withMDCContext(final Map<String, String> mdcContext, final Runnable runnable) {
        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
            runnable.run();
            MDC.clear();
        } else {
            runnable.run();
        }
    }

    /**
     * Safe mdc get optional.
     *
     * @param key the key
     * @return the optional
     */
    default Optional<String> getMDC(final String key) {
        if (MDC.getMDCAdapter() != null) {
            return Optional.ofNullable(MDC.get(key));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Safe mdc get optional.
     *
     * @param key the key
     * @return the optional
     */
    default Optional<String> getMDC(final MDCKeys.QueryableMDCKey key) {
        return this.getMDC(key.getKey());
    }

    /**
     * Put to mdc.
     *
     * @param key   the key
     * @param value the value
     */
    default void putMDC(final String key, final String value) {
        if (MDC.getMDCAdapter() != null) {
            MDC.put(key, value);
        }
    }

    /**
     * Put.
     *
     * @param key   the key
     * @param value the value
     */
    default void putMDC(final MDCKeys.QueryableMDCKey key, final String value) {
        this.putMDC(key.getKey(), value);
    }

    /**
     * Dump mdc string.
     *
     * @return the string
     */
    static String dumpMDC() {
        final StringBuilder data = new StringBuilder();
        data.append("== MDC ==\n");
        for (final Map.Entry<String, String> entry : MDC.getCopyOfContextMap().entrySet()) {
            data.append(String.format("%s=%s\n", entry.getKey(), entry.getValue()));
        }
        data.append("=========");
        return data.toString();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    static MDCContext getInstance() {
        return new MDCContext() {
        };
    }
}
