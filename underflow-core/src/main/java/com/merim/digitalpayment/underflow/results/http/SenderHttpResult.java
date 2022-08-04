package com.merim.digitalpayment.underflow.results.http;

import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;

import java.util.function.Consumer;

/**
 * SenderHttpResult.
 *
 * @author Pierre Adam
 * @since 22.07.18
 */
public class SenderHttpResult extends BaseHttpResult {

    /**
     * The Logic.
     */
    private final Consumer<HttpServerExchange> logic;

    /**
     * Instantiates a new Result.
     *
     * @param httpCode the http code
     * @param logic    the logic
     */
    public SenderHttpResult(final int httpCode, final Consumer<Sender> logic) {
        super(httpCode);
        this.logic = exchange -> logic.accept(exchange.getResponseSender());
    }

    @Override
    protected Consumer<HttpServerExchange> getLogic() {
        return this.logic;
    }
}
