package com.merimdigitalmedia.underflow;

import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * V2.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class FlowHandler implements HttpHandler {

    /**
     * The Logger.
     */
    final protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        final ContextHandler context = new ContextHandler(this, exchange);

        if (context.isValid()) {
            context.execute();
        } else {
            exchange.setStatusCode(404)
                    .endExchange();
        }
    }

    /**
     * Dispatch to a worker thread.
     *
     * @param exchange the exchange
     * @param runnable the runnable
     */
    protected void dispatch(final HttpServerExchange exchange,
                            final Runnable runnable) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(runnable);
        }
    }

    /**
     * Block the IO to perform data operations.
     *
     * @param exchange the exchange
     * @param runnable the runnable
     */
    protected void block(final HttpServerExchange exchange,
                         final Runnable runnable) {
        if (!exchange.isBlocking()) {
            exchange.startBlocking();
            runnable.run();
        }
    }

    /**
     * Dispatch and block.
     *
     * @param exchange the exchange
     * @param runnable the runnable
     */
    protected void dispatchAndBlock(final HttpServerExchange exchange,
                                    final Runnable runnable) {
        this.dispatch(exchange, () -> this.block(exchange, runnable));
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    protected void ok(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 200, exchangeData);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    protected void created(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 201, exchangeData);
    }

    /**
     * End the request with a status 204 No Content.
     *
     * @param exchange the exchange
     */
    protected void noContent(final HttpServerExchange exchange) {
        this.result(exchange, 204, sender -> {
        });
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    protected void badRequest(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 400, exchangeData);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    protected void forbidden(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 403, exchangeData);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    protected void notFound(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 404, exchangeData);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    protected void internalServerError(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 500, exchangeData);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param exchange     the exchange
     * @param exchangeData the exchange data
     */
    protected void serviceUnavailable(final HttpServerExchange exchange, final Consumer<Sender> exchangeData) {
        this.result(exchange, 503, exchangeData);
    }

    /**
     * Ends the request with the given status.
     *
     * @param exchange     the exchange
     * @param code         the code
     * @param exchangeData the exchange data
     */
    protected void result(final HttpServerExchange exchange, final int code, final Consumer<Sender> exchangeData) {
        exchange.setStatusCode(code);
        exchangeData.accept(exchange.getResponseSender());
        exchange.endExchange();
    }
}
