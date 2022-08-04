package com.merim.digitalpayment.underflow.mdc;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * MDCInterceptor.
 *
 * @author Pierre Adam
 * @since 21.09.29
 */
public class MDCInterceptor implements Consumer<HttpServerExchange> {

    /**
     * The constant INSTANCE.
     */
    private static final MDCInterceptor INSTANCE = new MDCInterceptor();

    private final MDCContext mdcContext;

    /**
     * The Mdc request consumers.
     */
    private final List<BiConsumer<HttpServerExchange, MDCContext>> mdcRequestConsumers;

    /**
     * Instantiates a new Request mdc.
     */
    private MDCInterceptor() {
        this.mdcContext = MDCContext.getInstance();
        this.mdcRequestConsumers = new ArrayList<>();

        this.register((exchange, mdcContext) -> {
            final ServerConnection connection = exchange.getConnection();
            mdcContext.putMDC(MDCKeys.Connection.IO_THREAD, connection.getIoThread().toString());
            mdcContext.putMDC(MDCKeys.Connection.PEER_ADDRESS, connection.getPeerAddress().toString());
            mdcContext.putMDC(MDCKeys.Request.METHOD, exchange.getRequestMethod().toString());
            mdcContext.putMDC(MDCKeys.Request.URL, exchange.getRequestURL());
            mdcContext.putMDC(MDCKeys.Request.QUERY_STRING, exchange.getQueryString());
            mdcContext.putMDC(MDCKeys.Request.HOST_NAME, exchange.getHostName());
            mdcContext.putMDC(MDCKeys.Request.HOST_PORT, String.format("%d", exchange.getHostPort()));
        });
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MDCInterceptor getInstance() {
        return MDCInterceptor.INSTANCE;
    }

    /**
     * Register.
     *
     * @param consumer the consumer
     */
    public void register(final BiConsumer<HttpServerExchange, MDCContext> consumer) {
        this.mdcRequestConsumers.add(consumer);
    }

    /**
     * Register.
     *
     * @param consumer the consumer
     */
    public void register(final Consumer<HttpServerExchange> consumer) {
        this.mdcRequestConsumers.add((exchange, mdcContext) -> consumer.accept(exchange));
    }

    @Override
    public void accept(final HttpServerExchange exchange) {
        if (MDC.getMDCAdapter() != null && !this.mdcContext.getMDC(MDCKeys.Request.UID).isPresent()) {
            this.mdcContext.putMDC(MDCKeys.Request.UID, UUID.randomUUID().toString());
            this.mdcRequestConsumers.forEach(consumer -> consumer.accept(exchange, this.mdcContext));
        }
    }
}
