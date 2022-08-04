package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.handlers.flows.assets.AssetLoader;
import com.merim.digitalpayment.underflow.annotation.method.GET;
import com.merim.digitalpayment.underflow.annotation.routing.Named;
import com.merim.digitalpayment.underflow.annotation.routing.Path;
import com.merim.digitalpayment.underflow.annotation.security.Secured;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.results.http.InputStreamHttpResult;
import com.merim.digitalpayment.underflow.security.FlowSecurity;
import io.undertow.util.StatusCodes;

import java.io.InputStream;

/**
 * FlowAssetsHandler.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
public class FlowAssetsHandler extends FlowHandler {

    /**
     * The Resource base path.
     */
    private final AssetLoader assetLoader;

    /**
     * Instantiates a new Flow assets handler.
     *
     * @param assetLoader the asset loader
     */
    public FlowAssetsHandler(final AssetLoader assetLoader) {
        this(assetLoader, null);
    }

    /**
     * Instantiates a new Flow assets handler.
     *
     * @param assetLoader  the asset loader
     * @param flowSecurity the flow security
     */
    public FlowAssetsHandler(final AssetLoader assetLoader, final FlowSecurity<?, ?> flowSecurity) {
        super(flowSecurity);
        this.assetLoader = assetLoader;
    }

    /**
     * Gets asset.
     *
     * @param path the path
     * @return the asset
     */
    @GET
    @Path("/(?<path>.+)")
    @Secured
    public Result getAsset(@Named("path") final String path) {
        final InputStream inputStream = this.assetLoader.open(path);
        if (inputStream == null) {
            return this.onNotFound();
        } else {
            return new InputStreamHttpResult(StatusCodes.OK, inputStream);
        }
    }
}
