package com.merim.digitalpayment.underflow.freemarker;

import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * MultiSourceTemplateLoader.
 *
 * @author Pierre Adam
 * @since 26.02.16
 */
public class MultiSourceTemplateLoader implements TemplateLoader {

    /**
     * The Loaders.
     */
    private final List<TemplateLoader> loaders;

    /**
     * Instantiates a new Multi source template loader.
     */
    public MultiSourceTemplateLoader() {
        this.loaders = new ArrayList<>();
    }

    /**
     * Add template source.
     *
     * @param loader the loader
     * @return the multi source template loader
     */
    public MultiSourceTemplateLoader addSource(final TemplateLoader loader) {
        this.loaders.add(loader);

        return this;
    }

    @Override
    public Object findTemplateSource(final String name) throws IOException {
        for (final TemplateLoader loader : this.loaders) {
            final Object templateSource = loader.findTemplateSource(name);

            if (templateSource != null) {
                return new SourceContainer(loader, templateSource);
            }
        }
        return null;
    }

    @Override
    public long getLastModified(final Object templateSource) {
        if (templateSource instanceof final MultiSourceTemplateLoader.SourceContainer sourceContainer) {
            return sourceContainer.loader.getLastModified(sourceContainer.templateSource);
        }

        throw new IllegalArgumentException("templateSource must be an instance of SourceContainer");
    }

    @Override
    public Reader getReader(final Object templateSource, final String encoding) throws IOException {
        if (templateSource instanceof final MultiSourceTemplateLoader.SourceContainer sourceContainer) {
            return sourceContainer.loader.getReader(sourceContainer.templateSource, encoding);
        }

        throw new IllegalArgumentException("templateSource must be an instance of SourceContainer");
    }

    @Override
    public void closeTemplateSource(final Object templateSource) throws IOException {
        if (templateSource instanceof final MultiSourceTemplateLoader.SourceContainer sourceContainer) {
            sourceContainer.loader.closeTemplateSource(sourceContainer.templateSource);
        }

        throw new IllegalArgumentException("templateSource must be an instance of SourceContainer");
    }

    /**
     * The type Source container.
     */
    private record SourceContainer(TemplateLoader loader, Object templateSource) {
    }
}
