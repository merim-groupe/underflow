package com.merim.digitalpayment.underflow.handlers.flows.assets;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * AssetsRepresentation.
 *
 * @author Pierre Adam
 * @since 23.07.05
 */
public interface AssetRepresentation {

    /**
     * Retrieves the ETag of the asset.
     * <p>
     * The ETag is typically a unique identifier or hash that represents the
     * current state of the asset, used to verify its integrity or check for changes.
     *
     * @return the ETag of the asset as a String
     */
    String getEtag();

    /**
     * Opens the asset and returns its content as an {@code InputStream}.
     * <p>
     * This method provides access to the underlying data of the asset. The caller
     * is responsible for closing the returned {@code InputStream} after use to
     * prevent resource leaks.
     *
     * @return an {@code InputStream} containing the content of the asset
     */
    InputStream open();

    /**
     * Retrieves the path of the asset.
     * <p>
     * The path typically represents the location of the asset within the
     * context of the implementation, such as a file system path or a
     * resource path.
     *
     * @return the path of the asset as a String
     */
    String getPath();

    /**
     * Retrieves the content type of the asset based on its path.
     * <p>
     * This method attempts to determine the content type by probing the file system
     * for the file's MIME type using its path. In case the content type cannot be
     * determined or an I/O error occurs, it returns an empty {@code Optional}.
     *
     * @return an {@code Optional} containing the determined content type, or {@code Optional.empty()} if
     * the content type could not be determined or an error occurred.
     */
    default Optional<String> getContentType() {
        try {
            return Optional.ofNullable(Files.probeContentType(Paths.get(this.getPath())));
        } catch (final IOException ignore) {
            return Optional.empty();
        }
    }
}
