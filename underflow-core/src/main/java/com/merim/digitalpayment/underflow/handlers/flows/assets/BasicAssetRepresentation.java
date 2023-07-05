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
     * Instantiates a new Basic asset representation.
     *
     * @param etag           the etag
     * @param streamSupplier the stream supplier
     */
    public BasicAssetRepresentation(final String etag, final Supplier<InputStream> streamSupplier) {
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
}
