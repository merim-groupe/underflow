package com.merim.digitalpayment.underflow.sample;

import com.merim.digitalpayment.underflow.attachments.UnderflowKeys;
import com.merim.digitalpayment.underflow.handlers.flows.FlowDTOWrapperTemplateHandler;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.sample.dto.SampleDTOWrapperBuilder;
import com.merim.digitalpayment.underflow.sample.security.MySecurity;
import io.undertow.server.HttpServerExchange;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ResourceIntensiveHandler.
 *
 * @author Pierre Adam
 * @since 26.03.10
 */
@Path("/heavy")
public class ResourceIntensiveHandler extends FlowDTOWrapperTemplateHandler {

    @Getter
    private final ExecutorService customExecutor;

    /**
     * Instantiates a new Test handler.
     */
    public ResourceIntensiveHandler() {
        super("/templates", new MySecurity(), new SampleDTOWrapperBuilder());

        final AtomicInteger threadCounter = new AtomicInteger(0);
        this.customExecutor = Executors.newFixedThreadPool(10, runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setName("Custom-Worker-Thread-" + threadCounter.incrementAndGet());
            return thread;
        });
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) {
        exchange.putAttachment(UnderflowKeys.WORKER_EXECUTOR_KEY, this.customExecutor);
        exchange.putAttachment(UnderflowKeys.RESPONSE_EXECUTOR_KEY, this.customExecutor);

        super.handleRequest(exchange);
    }

    @Operation(hidden = true)
    @Produces(MediaType.TEXT_HTML)
    @GET
    @Path("")
    // AppLanguage.Converter.class can be set at the application level using: Converters.addConverter(new AppLanguage.Converter());
    public CompletableFuture<Result> heavyWork(@Context final HttpServerExchange exchange) {
        this.logger.info("Starting work");
        return CompletableFuture.supplyAsync(() -> {
            this.logger.info("Starting actual work");
            try {
                Thread.sleep(30000);
            } catch (final InterruptedException ignore) {
            }
            this.logger.info("Ending work");

            return this.ok("Done");
        }, this.customExecutor);
    }
}
