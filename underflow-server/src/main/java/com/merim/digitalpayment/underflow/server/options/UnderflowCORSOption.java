package com.merim.digitalpayment.underflow.server.options;

import com.merim.digitalpayment.underflow.handlers.http.CORSHandler;
import com.merim.digitalpayment.underflow.handlers.http.IDontCareAboutCORSPleaseHelpHandler;
import io.undertow.server.HttpHandler;
import lombok.NonNull;

import java.util.function.Function;

/**
 * UnderflowLoggerOption.
 *
 * @author Pierre Adam
 * @since 24.04.23
 */
public class UnderflowCORSOption implements UnderflowOption {

    /**
     * The Chain handler.
     */
    private final Function<HttpHandler, HttpHandler> chainHandler;

    /**
     * Instantiates a new Underflow logger option.
     *
     * @param chainHandler the chain handler
     */
    protected UnderflowCORSOption(@NonNull final Function<HttpHandler, HttpHandler> chainHandler) {
        this.chainHandler = chainHandler;
    }

    /**
     * Enable underflow cors option.
     *
     * @return the underflow cors option
     */
    public static UnderflowCORSOption enable() {
        return new UnderflowCORSOption(CORSHandler::new);
    }

    /**
     * Enable underflow cors option.
     *
     * @param accessControlAllowOrigin the access control allow origin
     * @return the underflow cors option
     */
    public static UnderflowCORSOption enable(final String accessControlAllowOrigin) {
        return new UnderflowCORSOption(h -> new CORSHandler(h, accessControlAllowOrigin));
    }

    /**
     * Enable underflow cors option.
     *
     * @param accessControlAllowOrigin      the access control allow origin
     * @param accessControlAllowMethods     the access control allow methods
     * @param accessControlAllowHeaders     the access control allow headers
     * @param accessControlAllowCredentials the access control allow credentials
     * @return the underflow cors option
     */
    public static UnderflowCORSOption enable(final String accessControlAllowOrigin,
                                             final String accessControlAllowMethods,
                                             final String accessControlAllowHeaders,
                                             final boolean accessControlAllowCredentials) {
        return new UnderflowCORSOption(h -> new CORSHandler(h, accessControlAllowOrigin,
                accessControlAllowMethods, accessControlAllowHeaders, accessControlAllowCredentials));
    }

    /**
     * Enable easy mode underflow cors option.
     *
     * @return the underflow cors option
     */
    @Deprecated
    public static UnderflowCORSOption enableEasyCORS() {
        return new UnderflowCORSOption(h -> new IDontCareAboutCORSPleaseHelpHandler(h, true));
    }

    @Override
    public HttpHandler alterHandler(final String path, final HttpHandler handler) {
        return this.chainHandler.apply(handler);
    }
}
