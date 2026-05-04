package com.merim.digitalpayment.underflow.results;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.sse.ServerSentEventConnection;
import io.undertow.server.handlers.sse.ServerSentEventHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * ServerEventResult.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
@Slf4j
public class ServerEventResult implements Result {

    /**
     * The Server sent event handler.
     */
    private final ServerSentEventHandler serverSentEventHandler;

    /**
     * The And then.
     */
    private final Runnable andThen;

    /**
     * Instantiates a new Server event result.
     *
     * @param serverSentEventHandler the server sent event handler
     * @param andThen                the and then
     */
    public ServerEventResult(final ServerSentEventHandler serverSentEventHandler, final Runnable andThen) {
        this.serverSentEventHandler = serverSentEventHandler;
        this.andThen = andThen;
    }

    /**
     * Instantiates a new Server event result.
     *
     * @param serverSentEventHandler the server sent event handler
     */
    public ServerEventResult(final ServerSentEventHandler serverSentEventHandler) {
        this(serverSentEventHandler, () -> {
        });
    }

    @Override
    public void process(final HttpServerExchange exchange, final Method method) {
        try {
            this.serverSentEventHandler.handleRequest(exchange);
        } catch (final Exception e) {
            // If this part fails, the answer did not start yet.
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Runnable> andThen() {
        return Optional.of(() -> {
            // From here, the answer has started. So we should never throw exception. Otherwise, the framework will attempt to send a new response.
            try {
                this.andThen.run();
            } catch (final Exception e) {
                ServerEventResult.logger.error("Error while running logic assigned to ServerEventResult", e);
                for (final ServerSentEventConnection connection : this.serverSentEventHandler.getConnections()) {
                    try {
                        connection.close();
                    } catch (final IOException ignore) {
                    }
                }
            }
        });
    }
}
