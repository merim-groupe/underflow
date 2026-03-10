package com.merim.digitalpayment.underflow.executors;

import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UnderflowExecutor.
 *
 * @author Pierre Adam
 * @since 26.03.10
 */
@Getter
public class UnderflowExecutorContext implements Closeable {

    /**
     * The Response executor.
     */
    private final ExecutorService workerExecutor;

    /**
     * The Response executor.
     */
    private final ExecutorService responseExecutor;

    /**
     * Instantiates a new Underflow executor context.
     */
    public UnderflowExecutorContext() {
        final AtomicInteger workerThreadCounter = new AtomicInteger(0);
        final AtomicInteger responseThreadCounter = new AtomicInteger(0);

        this.workerExecutor = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors(), 16), runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setName("Underflow-Worker-" + workerThreadCounter.incrementAndGet());
            return thread;
        });

        this.responseExecutor = Executors.newFixedThreadPool(Math.max(Runtime.getRuntime().availableProcessors(), 16), runnable -> {
            final Thread thread = new Thread(runnable);
            thread.setName("Underflow-RespW-" + responseThreadCounter.incrementAndGet());
            return thread;
        });
    }


    @Override
    public void close() throws IOException {
        this.workerExecutor.shutdown();
        this.responseExecutor.shutdown();
    }
}
