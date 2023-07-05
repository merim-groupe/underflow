package com.merim.digitalpayment.underflow.handlers.flows.assets;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

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
     * The Etag cache.
     */
    final Map<String, String> etagCache;

    /**
     * Instantiates a new Resource asset loader.
     *
     * @param loader   the loader
     * @param basePath the base path
     */
    public ResourceAssetLoader(final Class<?> loader, final String basePath) {
        this.loader = loader;
        this.basePath = Paths.get(basePath);
        this.etagCache = null;
    }

    /**
     * Instantiates a new Resource asset loader.
     * Recommended to use UnderflowMapUtils.createLRUCacheMap(...) as etagCache.
     *
     * @param loader    the loader
     * @param basePath  the base path
     * @param etagCache the etag cache
     */
    public ResourceAssetLoader(final Class<?> loader, final String basePath, final Map<String, String> etagCache) {
        this.loader = loader;
        this.basePath = Paths.get(basePath);
        this.etagCache = etagCache;
    }

    @Override
    public Optional<AssetRepresentation> load(final String path) {
        final String fullPath = this.convertPathToResource(Paths.get(this.basePath.toString(), path));
        final String etag;

        if (this.etagCache.containsKey(fullPath)) {
            etag = this.etagCache.get(fullPath);
        } else {
            etag = this.getResourceEtag(fullPath);
            if (etag != null) {
                this.etagCache.put(fullPath, etag);
            }
        }

        if (etag == null) {
            return Optional.empty();
        }

        return Optional.of(new BasicAssetRepresentation(etag, () -> this.loader.getResourceAsStream(fullPath)));
    }

    /**
     * Convert path to resource string.
     *
     * @param path the path
     * @return the string
     */
    private String convertPathToResource(final Path path) {
        return path.toString().replaceAll(Pattern.quote(path.getFileSystem().getSeparator()), "/");
    }

    /**
     * Gets resource etag.
     *
     * @param path the path
     * @return the resource etag
     */
    private String getResourceEtag(final String path) {
        try (final InputStream resource = this.loader.getResourceAsStream(path)) {
            if (resource == null) {
                return null;
            }
            return DigestUtils.md5Hex(resource);
        } catch (final Exception e) {
            LoggerFactory.getLogger(this.getClass()).error("An error occurred while trying to open a resource.", e);
            return null;
        }
    }
}
