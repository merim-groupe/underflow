package com.merim.digitalpayment.underflow.attachments;

import io.undertow.util.AttachmentKey;

import java.util.concurrent.ExecutorService;

/**
 * UnderflowKeys.
 *
 * @author Pierre Adam
 * @since 26.03.10
 */
public class UnderflowKeys {

    /**
     * The constant WORKER_EXECUTOR_KEY.
     */
    public static final AttachmentKey<ExecutorService> WORKER_EXECUTOR_KEY =
            AttachmentKey.create(ExecutorService.class);

    /**
     * The constant RESPONSE_EXECUTOR_KEY.
     */
    public static final AttachmentKey<ExecutorService> RESPONSE_EXECUTOR_KEY =
            AttachmentKey.create(ExecutorService.class);

    /**
     * The constant AFTER_RESPONSE_EXECUTOR_KEY.
     */
    public static final AttachmentKey<ExecutorService> AFTER_RESPONSE_EXECUTOR_KEY =
            AttachmentKey.create(ExecutorService.class);
}
