package com.merim.digitalpayment.underflow.mdc;

import java.io.Closeable;
import java.io.IOException;

/**
 * MDCServerContext.
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
