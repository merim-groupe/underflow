package com.merimdigitalmedia.underflow.handlers.flows;

import com.merimdigitalmedia.underflow.handlers.context.ContextHandler;
import com.merimdigitalmedia.underflow.handlers.flows.answers.FlowSenderAnswer;
import com.merimdigitalmedia.underflow.handlers.flows.answers.FlowStandardAnswer;
import com.merimdigitalmedia.underflow.mdc.MDCContext;
import com.merimdigitalmedia.underflow.mdc.MDCInterceptor;
import io.undertow.io.Sender;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.function.Consumer;

/**
 * V2.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class FlowHandler implements HttpHandler, MDCContext, FlowSenderAnswer, FlowStandardAnswer {

    /**
     * The Logger.
     */
    final protected Logger logger;

    /**
     * Instantiates a new Flow handler.
     */
    public FlowHandler() {
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {
        MDCInterceptor.getInstance().accept(exchange);
        final ContextHandler context = new ContextHandler(this, exchange);

        if (context.isValid()) {
            this.exchangeDelegation(exchange, context::execute);
        } else {
            exchange.setStatusCode(404)
                    .endExchange();
        }
    }

    /**
     * Dispatch.
     *
     * @param exchange the exchange
     * @param runnable the runnable
     */
    protected void dispatch(final HttpServerExchange exchange,
                            final Runnable runnable) {
        this.dispatch(exchange, true, runnable);
    }

    /**
     * Dispatch unsafe.
     *
     * @param exchange the exchange
     * @param runnable the runnable
     */
    protected void dispatchUnsafe(final HttpServerExchange exchange,
                                  final Runnable runnable) {
        this.dispatch(exchange, false, runnable);
    }

    /**
     * Dispatch to a worker thread.
     *
     * @param exchange      the exchange
     * @param closeExchange the close exchange
     * @param runnable      the runnable
     */
    private void dispatch(final HttpServerExchange exchange,
                          final boolean closeExchange,
                          final Runnable runnable) {
        if (exchange.isInIoThread()) {
            final Map<String, String> mdcContext = this.popMDCContext();
            exchange.dispatch(() ->
                    this.exchangeDelegation(exchange, closeExchange, () ->
                            this.withMDCContext(mdcContext, () -> {
                                        runnable.run();
                                        if (closeExchange && !exchange.isComplete()) {
                                            exchange.endExchange();
                                        }
                                    }
                            )
                    )
            );
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
     * Dispatch and block.
     *
     * @param exchange the exchange
     * @param runnable the runnable
     */
    protected void dispatchUnsafeAndBlock(final HttpServerExchange exchange,
                                          final Runnable runnable) {
        this.dispatchUnsafe(exchange, () -> this.block(exchange, runnable));
    }

    /**
     * Delegate the handling to the exchange to the runnable.
     * If an exception is thrown, the exchange will be closed.
     *
     * @param exchange the exchange
     * @param runnable the runnable
     */
    private void exchangeDelegation(final HttpServerExchange exchange, final Runnable runnable) {
        this.exchangeDelegation(exchange, true, runnable);
    }

    /**
     * Delegate the handling to the exchange to the runnable.
     * If an exception is thrown, the exchange will be closed.
     *
     * @param exchange      the exchange
     * @param closeExchange the close exchange
     * @param runnable      the runnable
     */
    private void exchangeDelegation(final HttpServerExchange exchange, final boolean closeExchange, final Runnable runnable) {
        try {
            runnable.run();
        } catch (final Exception e) {
            this.logger.error("An uncaught error occurred on the handler method and escalated to Underflow.", e);
        } finally {
            if (!exchange.isDispatched() && !exchange.isComplete() && closeExchange) {
                if (!exchange.isResponseStarted()) {
                    exchange.setStatusCode(500);
                }
                exchange.endExchange();
            }
        }
    }

    @Override
    public void result(final HttpServerExchange exchange, final int code, final Consumer<Sender> exchangeData) {
        exchange.setStatusCode(code);
        exchangeData.accept(exchange.getResponseSender());
        exchange.endExchange();
    }
}
