package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.handlers.flows.assets.AssetLoader;
import com.merim.digitalpayment.underflow.handlers.flows.assets.AssetRepresentation;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.results.http.InputStreamHttpResult;
import com.merim.digitalpayment.underflow.security.FlowSecurity;
import com.merim.digitalpayment.underflow.security.annotations.Secured;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HEAD;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.Optional;

/**
 * FlowAssetsHandler.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
public abstract class FlowAssetsHandler extends FlowHandler {

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
     * @param exchange the exchange
     * @param path     the path
     * @return the asset
     */
    @GET
    @Path("/{path:.+}")
    @Secured
    public Result getAsset(final HttpServerExchange exchange, @PathParam("path") final String path) {
        final Optional<AssetRepresentation> assetRepresentation = this.assetLoader.load(path);
        if (assetRepresentation.isPresent()) {
            final AssetRepresentation asset = assetRepresentation.get();

            if (asset.getEtag() != null) {
                final HeaderValues ifMatchValues = exchange.getRequestHeaders().get(Headers.IF_MATCH);
                if (ifMatchValues != null) {
                    final String etag = ifMatchValues.get(0);

                    if (!etag.equals(asset.getEtag())) {
                        return this.result(StatusCodes.PRECONDITION_FAILED, "")
                                .withHeader(Headers.ETAG, asset.getEtag())
                                .withHeader(Headers.CACHE_CONTROL, "public, max-age=0, must-revalidate");
                    }
                }

                final HeaderValues ifNoneMatchValues = exchange.getRequestHeaders().get(Headers.IF_NONE_MATCH);
                if (ifNoneMatchValues != null) {
                    final String etag = ifNoneMatchValues.get(0);
                    if (etag.equals(asset.getEtag())) {
                        return this.result(StatusCodes.NOT_MODIFIED, "")
                                .withHeader(Headers.ETAG, asset.getEtag())
                                .withHeader(Headers.CACHE_CONTROL, "public, max-age=0, must-revalidate");
                    }
                }

                if (exchange.getRequestMethod().toString().equals("HEAD")) {
                    return this.ok("")
                            .withHeader(Headers.ETAG, asset.getEtag())
                            .withHeader(Headers.CACHE_CONTROL, "public, max-age=0, must-revalidate");
                } else {
                    return new InputStreamHttpResult(StatusCodes.OK, asset.open())
                            .withHeader(Headers.ETAG, asset.getEtag())
                            .withHeader(Headers.CACHE_CONTROL, "public, max-age=0, must-revalidate");
                }
            }

            if (exchange.getRequestMethod().toString().equals("HEAD")) {
                return this.ok("");
            } else {
                return new InputStreamHttpResult(StatusCodes.OK, asset.open());
            }
        } else {
            return this.onNotFound();
        }
    }

    /**
     * Gets asset head.
     *
     * @param exchange the exchange
     * @param path     the path
     * @return the asset head
     */
    @HEAD
    @Path("/{path:.+}")
    @Secured
    public Result getAssetHead(final HttpServerExchange exchange, @PathParam("path") final String path) {
        return this.getAsset(exchange, path);
    }
}
