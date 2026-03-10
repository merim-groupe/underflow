package com.merim.digitalpayment.underflow.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * AttachmentAddingHandler.
 *
 * @author Pierre Adam
 * @since 26.03.10
 */
public class ExchangeActionsHandler extends PassthroughsHandler {

    /**
     * The Attachments.
     */
    private final List<Consumer<HttpServerExchange>> actions;

    /**
     * Instantiates a new Passthrough handler.
     *
     * @param underlying the underlying
     */
    public ExchangeActionsHandler(final HttpHandler underlying) {
        super(underlying);
        this.actions = new ArrayList<>();
    }

    /**
     * Add action.
     *
     * @param action the action
     */
    public void addAction(final Consumer<HttpServerExchange> action) {
        this.actions.add(action);
    }

    /**
     * Add attachment.
     *
     * @param <T>   the type parameter
     * @param key   the key
     * @param value the value
     */
    public <T> void addAttachment(final AttachmentKey<T> key, final T value) {
        this.addAction(exchange -> exchange.putAttachment(key, value));
    }

    @Override
    protected void interceptRequest(final HttpServerExchange exchange) {
        this.actions.forEach(action -> action.accept(exchange));
    }
}
