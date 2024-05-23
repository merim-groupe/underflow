package com.merim.digitalpayment.underflow.server;

import com.merim.digitalpayment.underflow.server.options.UnderflowOption;
import io.undertow.server.HttpHandler;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * HandlerData.
 *
 * @author Pierre Adam
 * @since 24.04.23
 */
@Getter
public class HandlerData {

    /**
     * The Handler.
     */
    private final HttpHandler handler;

    /**
     * The Options.
     */
    private final List<UnderflowOption> options;

    /**
     * Instantiates a new Handler data.
     *
     * @param handler the handler
     * @param options the options
     */
    public HandlerData(final HttpHandler handler, final UnderflowOption... options) {
        this.handler = handler;
        this.options = new ArrayList<>(Arrays.asList(options));
        Collections.reverse(this.options);
    }
}
