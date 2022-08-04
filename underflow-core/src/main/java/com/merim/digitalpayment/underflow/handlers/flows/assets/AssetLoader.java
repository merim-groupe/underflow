package com.merim.digitalpayment.underflow.handlers.flows.assets;

import java.io.InputStream;

/**
 * AssetLoader.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
public interface AssetLoader {

    /**
     * Open input stream.
     *
     * @param path the path
     * @return the input stream
     */
    InputStream open(String path);
}
