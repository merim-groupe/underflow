package com.merim.digitalpayment.underflow.handlers.flows.assets;

import java.util.Optional;

/**
 * AssetLoader.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
public interface AssetLoader {

    /**
     * Load assets representation.
     *
     * @param path the path
     * @return the assets representation
     */
    Optional<AssetRepresentation> load(String path);
}
