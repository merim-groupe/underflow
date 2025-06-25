package com.merim.digitalpayment.underflow.handlers.flows.assets;

import java.io.InputStream;
import java.util.function.Supplier;

/**
 * BasicAssetRepresentation.
 *
 * @author Pierre Adam
 * @since 23.07.05
 */
public class BasicAssetRepresentation implements AssetRepresentation {

    /**
     * The Etag.
     */
    private final String etag;

    /**
     * The Stream supplier.
     */
    private final Supplier<InputStream> streamSupplier;

    /**
     * The Path.
     */
    private final String path;

    /**
     * Constructs a new BasicAssetRepresentation instance with the specified path, ETag, and stream supplier.
     *
     * @param path           the path of the asset
     * @param etag           the ETag of the asset, representing its unique identifier or state
     * @param streamSupplier a supplier that provides an InputStream for accessing the asset's content
     */
    public BasicAssetRepresentation(final String path, final String etag, final Supplier<InputStream> streamSupplier) {
        this.path = path;
        this.etag = etag;
        this.streamSupplier = streamSupplier;
    }

    @Override
    public String getEtag() {
        return this.etag;
    }

    @Override
    public InputStream open() {
        return this.streamSupplier.get();
    }

    @Override
    public String getPath() {
        return this.path;
    }
}
