package com.merim.digitalpayment.underflow.handlers.flows;

import com.merim.digitalpayment.underflow.handlers.flows.assets.AssetLoader;
import com.merim.digitalpayment.underflow.handlers.flows.assets.AssetRepresentation;
import com.merim.digitalpayment.underflow.results.Result;
import com.merim.digitalpayment.underflow.results.http.HttpResult;
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
import jakarta.ws.rs.core.Context;
import org.eclipse.microprofile.openapi.annotations.Operation;

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
     * Gets cache control rule.
     *
     * @return the cache control rule
     */
    protected String getCacheControlRule() {
        return "public, max-age=0, must-revalidate";
    }

    /**
     * Gets asset.
     *
     * @param exchange the exchange
     * @param path     the path
     * @return the asset
     */
    @Operation(hidden = true)
    @GET
    @Path("/{path:.+}")
    @Secured
    public Result getAsset(@Context final HttpServerExchange exchange,
                           @PathParam("path") final String path) {
        final Optional<AssetRepresentation> assetRepresentation = this.assetLoader.load(path);
        if (assetRepresentation.isPresent()) {
            final AssetRepresentation asset = assetRepresentation.get();

            if (asset.getEtag() != null) {
                final HeaderValues ifMatchValues = exchange.getRequestHeaders().get(Headers.IF_MATCH);

                if (ifMatchValues != null) {
                    final String etag = ifMatchValues.get(0);

                    if (!etag.equals(asset.getEtag())) {
                        return this.applyEtagAndContentTypeHeaders(this.result(StatusCodes.PRECONDITION_FAILED, ""), asset);
                    }
                }

                final HeaderValues ifNoneMatchValues = exchange.getRequestHeaders().get(Headers.IF_NONE_MATCH);
                if (ifNoneMatchValues != null) {
                    final String etag = ifNoneMatchValues.get(0);
                    if (etag.equals(asset.getEtag())) {
                        return this.applyEtagAndContentTypeHeaders(this.result(StatusCodes.NOT_MODIFIED, ""), asset);
                    }
                }

                if (exchange.getRequestMethod().toString().equals("HEAD")) {
                    return this.applyEtagAndContentTypeHeaders(this.ok(""), asset);
                } else {
                    return this.applyEtagAndContentTypeHeaders(new InputStreamHttpResult(StatusCodes.OK, asset.open()), asset);
                }
            }

            if (exchange.getRequestMethod().toString().equals("HEAD")) {
                return this.applyContentTypeHeader(this.ok(""), asset);
            } else {
                return this.applyContentTypeHeader(new InputStreamHttpResult(StatusCodes.OK, asset.open()), asset);
            }
        } else {
            return this.onNotFound();
        }
    }

    /**
     * Apply etag header http result.
     *
     * @param result the result
     * @param asset  the asset
     * @return the http result
     */
    private HttpResult applyEtagHeader(final HttpResult result, final AssetRepresentation asset) {
        return result.withHeader(Headers.ETAG, asset.getEtag())
                .withHeader(Headers.CACHE_CONTROL, this.getCacheControlRule());
    }

    /**
     * Apply content type header.
     *
     * @param result the result
     * @param asset  the asset
     */
    private HttpResult applyContentTypeHeader(final HttpResult result, final AssetRepresentation asset) {
        asset.getContentType().ifPresent(contentType -> result.withHeader(Headers.CONTENT_TYPE, contentType));

        return result;
    }

    /**
     * Apply etag and content type headers.
     *
     * @param result the result
     * @param asset  the asset
     */
    private HttpResult applyEtagAndContentTypeHeaders(final HttpResult result, final AssetRepresentation asset) {
        return this.applyContentTypeHeader(this.applyEtagHeader(result, asset), asset);
    }

    /**
     * Gets asset head.
     *
     * @param exchange the exchange
     * @param path     the path
     * @return the asset head
     */
    @Operation(hidden = true)
    @HEAD
    @Path("/{path:.+}")
    @Secured
    public Result getAssetHead(@Context final HttpServerExchange exchange,
                               @PathParam("path") final String path) {
        return this.getAsset(exchange, path);
    }
}
