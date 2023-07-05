package com.merim.digitalpayment.underflow.handlers.flows.assets;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.util.Optional;

/**
 * FileAssetLoader.
 *
 * @author Pierre Adam
 * @since 23.06.14
 */
public class FileAssetLoader implements AssetLoader {

    /**
     * The Folder.
     */
    private final File folder;

    /**
     * Instantiates a new File asset loader.
     *
     * @param folder the path
     */
    public FileAssetLoader(final File folder) {
        this.folder = folder;

        if (!this.folder.exists()) {
            throw new RuntimeException("Invalid folder for assets! Not such directory.");
        }

        if (!this.folder.isDirectory()) {
            throw new RuntimeException("Invalid folder for assets! Not a directory.");
        }
    }

    @Override
    public Optional<AssetRepresentation> load(final String path) {
        final File file = new File(this.folder, path);

        if (!file.exists()) {
            return Optional.empty();
        }

        final String etag;
        try (final InputStream stream = Files.newInputStream(file.toPath())) {
            etag = DigestUtils.md5Hex(stream);
        } catch (final IOException e) {
            LoggerFactory.getLogger(FileAssetLoader.class).error("An error occurred while trying to open a file.", e);
            return Optional.empty();
        }

        return Optional.of(new BasicAssetRepresentation(etag, () -> {
            try {
                return new FileInputStream(file);
            } catch (final FileNotFoundException e) {
                LoggerFactory.getLogger(FileAssetLoader.class).error("An error occurred while trying to open a file.", e);
                throw new RuntimeException(e);
            }
        }));
    }
}
