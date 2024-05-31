package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import com.merim.digitalpayment.underflow.server.UnderflowServerImpl;
import com.merim.digitalpayment.underflow.server.modules.UnderflowApplicationModule;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.openapi.api.OpenApiConfig;
import io.smallrye.openapi.api.OpenApiConfigImpl;
import io.smallrye.openapi.runtime.io.Format;
import io.smallrye.openapi.runtime.io.OpenApiSerializer;
import io.smallrye.openapi.runtime.scanner.OpenApiAnnotationScanner;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.jboss.jandex.CompositeIndex;
import org.jboss.jandex.IndexReader;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * OpenApiServerModule.
 *
 * @author Pierre Adam
 * @since 24.05.27
 */
@Slf4j
public class OpenApiApplicationModule implements UnderflowApplicationModule {

    /**
     * The constant JANDEX_INDEX.
     */
    private static final String JANDEX_INDEX = "META-INF/jandex.idx";

    /**
     * The Open api.
     */
    private final OpenAPI openAPI;

    /**
     * Instantiates a new Open api server module.
     */
    public OpenApiApplicationModule() {
        this.openAPI = null;
    }

    private static List<URL> findIndexFiles(final String... indexPaths) throws IOException {
        final List<URL> result = new ArrayList<>();

        for (final String indexPath : indexPaths) {
            final Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(indexPath);
            while (urls.hasMoreElements()) {
                result.add(urls.nextElement());
            }
        }

        return result;
    }

    private IndexView createIndexView(final List<URL> indexURLs) throws IOException {
        final List<IndexView> indices = new ArrayList<>();

        for (final URL indexURL : indexURLs) {
            try (final InputStream indexIS = indexURL.openStream()) {
                OpenApiApplicationModule.logger.info("Adding Jandex index at {0}", indexURL);
                indices.add(new IndexReader(indexIS).read());
            } catch (final IOException ex) {
                throw new IOException("Attempted to read from previously-located index file "
                        + indexURL + " but the file cannot be found", ex);
            }
        }

        return indices.size() == 1 ? indices.get(0) : CompositeIndex.create(indices);
    }

    @Override
    public int priority() {
        return 1000;
    }

    @Override
    public void register(final UnderflowServerBuilder builder) {
//        builder.addHandler("/openapi", new OpenApiHandler());
    }

    @Override
    public void onServerCreated(final UnderflowServer server) {
        if (server instanceof UnderflowServerImpl) {
            final UnderflowServerImpl serverImpl = (UnderflowServerImpl) server;
            this.test(serverImpl);
        }
    }

    /**
     * Load class.
     *
     * @param aClass  the a class
     * @param indexer the indexer
     */
    private void loadClass(final Class<?> aClass,
                           final Indexer indexer) {
        this.loadClass(aClass, indexer, 0);
    }

    /**
     * Load class.
     *
     * @param aClass  the a class
     * @param indexer the indexer
     * @param depth   the depth
     */
    private void loadClass(final Class<?> aClass,
                           final Indexer indexer,
                           final int depth) {
        try {
            OpenApiApplicationModule.logger.info("Indexing {}", aClass);
            indexer.indexClass(aClass);

            final Class<?> superclass = aClass.getSuperclass();
            if (superclass != null && superclass != Object.class) {
                this.loadClass(superclass, indexer, depth + 1);
            }
        } catch (final IOException e) {
            OpenApiApplicationModule.logger.error("Unable to open {} for Jandex indexer.", aClass);
        }
    }

    /**
     * Test.
     *
     * @param server the server
     */
    private void test(final UnderflowServerImpl server) {
        final SmallRyeConfigBuilder configBuilder = new SmallRyeConfigBuilder();
        final OpenApiConfig config = new OpenApiConfigImpl(configBuilder.build());
        final IndexView indexView;

        try {
            final List<URL> indexFiles = OpenApiApplicationModule.findIndexFiles(OpenApiApplicationModule.JANDEX_INDEX);
            indexView = this.createIndexView(indexFiles);
//            indexView = new FilteredIndexView(index, config);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final OpenApiAnnotationScanner scanner = new OpenApiAnnotationScanner(config, indexView, new ArrayList<>());
        final OpenAPI oai = scanner.scan();  //this is the OpenAPI model

        // This is your OpenAPI specification as a String
        try {
            final String openAPISpec = OpenApiSerializer.serialize(oai, Format.YAML);
            System.out.println("OpenAPI Spec");
            System.out.println(openAPISpec);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
