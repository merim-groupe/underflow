package com.merim.digitalpayment.underflow.server;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * UnderflowServerFactory.
 *
 * @author Pierre Adam
 * @since 22.09.28
 */
public class ShutdownHandlingFactory {

    /**
     * Get shutdown handling.
     *
     * @return the shutdown handling
     */
    public static ShutdownHandling get() {
        final Iterator<ShutdownHandling> iterator = ServiceLoader.load(ShutdownHandling.class).iterator();

        if (!iterator.hasNext()) {
            throw new RuntimeException("No implementation of ShutdownHandling are available. Handling of shutdown is only available for Java8+");
        }

        return iterator.next();
    }
}
