package com.merim.digitalpayment.underflow.openapi;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.app.Mode;
import com.merim.digitalpayment.underflow.server.UnderflowServer;
import com.merim.digitalpayment.underflow.server.UnderflowServerBuilder;
import com.merim.digitalpayment.underflow.server.UnderflowServerImpl;
import com.merim.digitalpayment.underflow.server.modules.UnderflowServerModule;
import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfigBuilder;
import io.smallrye.openapi.api.OpenApiConfig;
import io.smallrye.openapi.api.OpenApiConfigImpl;
import io.smallrye.openapi.api.util.FilterUtil;
import io.smallrye.openapi.runtime.scanner.FilteredIndexView;
import io.smallrye.openapi.runtime.scanner.OpenApiAnnotationScanner;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.OASConfig;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.jboss.jandex.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OpenApiServerModule.
 *
 * @author Pierre Adam
 * @since 24.05.27
 */
@Slf4j
public class OpenApiServerModule implements UnderflowServerModule {

    /**
     * The constant PATH.
     */
    private static final Set<DotName> PATH_ANNOTATIONS = new TreeSet<>(Arrays.asList(
            DotName.createSimple("javax.ws.rs.Path"),
            DotName.createSimple("jakarta.ws.rs.Path")));

    /**
     * The constant JANDEX_INDEX.
     */
    private static final String JANDEX_INDEX = "META-INF/jandex.idx";

    /**
     * The Oas filters.
     */
    private final OASFilter[] oasFilters;

    /**
     * The Open api.
     */
    private OpenAPI openAPI;

    /**
     * Instantiates a new Open api server module.
     *
     * @param oasFilters the oas filters
     */
    public OpenApiServerModule(final OASFilter... oasFilters) {
        this.openAPI = null;
        this.oasFilters = oasFilters;
    }

    /**
     * Find index files list.
     *
     * @param indexPaths the index paths
     * @return the list
     * @throws IOException the io exception
     */
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

    /**
     * Create development index view index view.
     *
     * @return the index view
     * @throws IOException the io exception
     */
    private static IndexView createDevelopmentIndexView() throws IOException {
        final File projectDir = Paths.get(".").toAbsolutePath().normalize().toFile();
        final Indexer fileIndexer = new Indexer();

        final List<URL> jarIndexFiles = OpenApiServerModule.findIndexFiles(OpenApiServerModule.JANDEX_INDEX).stream()
                .filter(url -> url.getProtocol().equals("jar"))
                .collect(Collectors.toList());

        OpenApiServerModule.indexDirectory(projectDir, fileIndexer);

        return CompositeIndex.create(OpenApiServerModule.createIndexView(jarIndexFiles), fileIndexer.complete());
    }

    /**
     * Index directory.
     *
     * @param dir     the dir
     * @param indexer the indexer
     * @throws IOException the io exception
     */
    private static void indexDirectory(@NonNull final File dir, @NonNull final Indexer indexer) throws IOException {
        for (final File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                OpenApiServerModule.indexDirectory(file, indexer);
            } else if (file.getName().endsWith(".class")) {
                try (final FileInputStream fis = new FileInputStream(file)) {
                    indexer.index(fis);
                }
            }
        }
    }

    /**
     * Create index view index view.
     *
     * @param indexURLs the index ur ls
     * @return the index view
     * @throws IOException the io exception
     */
    private static IndexView createIndexView(final List<URL> indexURLs) throws IOException {
        final List<IndexView> indices = new ArrayList<>();

        for (final URL indexURL : indexURLs) {
            try (final InputStream indexIS = indexURL.openStream()) {
//                OpenApiApplicationModule.logger.info("Adding Jandex index at {}", indexURL);
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
        OpenApiServerModule.logger.error("REGISTERING OPENAPI MODULE");
        builder.addHandler(new OpenApiHandler(() -> this.openAPI));
    }

    @Override
    public void onServerCreated(final UnderflowServer server) {
        if (server instanceof UnderflowServerImpl) {
            final UnderflowServerImpl serverImpl = (UnderflowServerImpl) server;
            this.openAPI = this.createOpenAPI(serverImpl);
        }
    }

    /**
     * createOpenAPI.
     *
     * @param server the server
     * @return the open api
     */
    private OpenAPI createOpenAPI(final UnderflowServerImpl server) {
        final OpenApiConfig config = new OpenApiConfigImpl(new SmallRyeConfigBuilder().addDefaultSources().build());
        final IndexView indexView;

        try {
            if (Application.getMode() == Mode.DEV) {
                OpenApiServerModule.logger.info("OpenAPI Module in development mode. Filesystem will be scanned for compiled class.");
                indexView = OpenApiServerModule.createDevelopmentIndexView();
            } else {
                final List<URL> indexFiles = OpenApiServerModule.findIndexFiles(OpenApiServerModule.JANDEX_INDEX);
                indexView = OpenApiServerModule.createIndexView(indexFiles);
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final FilteredIndexView filteredIndexView = this.createFilteredIndexView(indexView, server);
        final OpenApiAnnotationScanner scanner = new OpenApiAnnotationScanner(config, filteredIndexView, new ArrayList<>());
        final OpenAPI openAPI = scanner.scan();

        this.runFilters(openAPI);

        return openAPI;
    }

    /**
     * Run filters.
     *
     * @param openAPI the open api
     */
    private void runFilters(final OpenAPI openAPI) {
        for (final OASFilter filter : this.oasFilters) {
            FilterUtil.applyFilter(filter, openAPI);
        }
    }

    /**
     * Gets jax rs resource classes.
     *
     * @param index the index
     * @return the jax rs resource classes
     */
    private Collection<ClassInfo> getAnnotatedClass(final IndexView index) {
        final Collection<AnnotationInstance> pathAnnotations = new ArrayList<>();

        for (final DotName dn : OpenApiServerModule.PATH_ANNOTATIONS) {
            pathAnnotations.addAll(index.getAnnotations(dn));
        }

        return pathAnnotations
                .stream()
                .map(AnnotationInstance::target)
                .filter(target -> target.kind() == AnnotationTarget.Kind.CLASS)
                .map(AnnotationTarget::asClass)
                .filter(classInfo -> {
                    if (!Modifier.isAbstract(classInfo.flags()) && !classInfo.isSynthetic()) {
                        return true;
                    }

                    return index.getAllKnownImplementors(classInfo.name())
                            .stream()
                            .anyMatch(cInfo -> !Modifier.isAbstract(cInfo.flags()) && !cInfo.isSynthetic());
                })
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ClassInfo::name))));
    }

    /**
     * Create filtered index view filtered index view.
     *
     * @param indexView the index view
     * @param server    the server
     * @return the filtered index view
     */
    private FilteredIndexView createFilteredIndexView(final IndexView indexView, final UnderflowServerImpl server) {
        final Map<String, String> configProperties = new HashMap<>();
        final List<String> serverActiveHandlers = server.getHandlers().values()
                .stream()
                .map(handlerData -> handlerData.getHandler().getClass())
                .map(Class::getName)
                .collect(Collectors.toList());

        final String exclude = this.getAnnotatedClass(indexView).stream()
                .filter(classInfo -> !serverActiveHandlers.contains(classInfo.name().toString()))
                .map(classInfo -> classInfo.name().toString())
                .collect(Collectors.joining(","));

        configProperties.put(OASConfig.SCAN_EXCLUDE_CLASSES, exclude);

        final OpenApiConfig config = new OpenApiConfigImpl(new SmallRyeConfigBuilder()
                .addDefaultSources()
                .withSources(new PropertiesConfigSource(configProperties, "underflow-openapi-autoresolve", 100))
                .build());

        return new FilteredIndexView(indexView, config);
    }
}
