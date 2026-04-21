package com.merim.digitalpayment.underflow.mdc;

import java.io.Closeable;
import java.io.IOException;

/**
 * The MDCServerContext class provides a mechanism for managing the lifecycle
 * of a Mapped Diagnostic Context (MDC) within a server environment. This class
 * ensures that MDC-specific data is properly managed and cleaned up when the
 * context is closed.
 * <p>
 * It is particularly useful in scenarios where diagnostic or contextual
 * information needs to be propagated and maintained across different threads
 * during the execution of server-side logic.
 * <p>
 * The class implements the {@link Closeable} interface, allowing it to be used
 * in try-with-resources blocks to ensure automatic cleanup of MDC data.
 *
 * @author Pierre Adam
 * @since 22.08.08
 */
public class MDCServerContext implements Closeable {

    /**
     * The Mdc context.
     */
    private final MDCContext mdcContext;

    /**
     * Instantiates a new Mdc server context.
     *
     * @param mdcContext the mdc context
     */
    public MDCServerContext(final MDCContext mdcContext) {
        this.mdcContext = mdcContext;
    }

    @Override
    public void close() throws IOException {
        if (this.mdcContext != null) {
            this.mdcContext.popMDCContext();
        }
    }
}
