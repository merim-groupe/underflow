package com.merim.digitalpayment.underflow.handlers.flows.assets;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ResourceAssetLoader.
 *
 * @author Pierre Adam
 * @since 22.07.21
 */
public class ResourceAssetLoader implements AssetLoader {

    /**
     * The Loader.
     */
    final Class<?> loader;

    /**
     * The Base path.
     */
    final Path basePath;

    /**
     * Instantiates a new Resource asset loader.
     *
     * @param loader   the loader
     * @param basePath the base path
     */
    public ResourceAssetLoader(final Class<?> loader, final String basePath) {
        this.loader = loader;
        this.basePath = Paths.get(basePath);
    }

    @Override
    public InputStream open(final String path) {
        final String fullPath = Paths.get(this.basePath.toString(), path).toString();
        
        InputStream in = this.loader.getResourceAsStream(fullPath);

        if (in == null) {
            in = this.loader.getClassLoader().getResourceAsStream(fullPath);
        }

        return in == null ? Thread.currentThread().getContextClassLoader().getResourceAsStream(fullPath) : in;
    }
}
