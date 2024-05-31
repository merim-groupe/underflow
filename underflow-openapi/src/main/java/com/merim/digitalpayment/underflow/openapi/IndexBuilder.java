package com.merim.digitalpayment.underflow.openapi;

import lombok.extern.slf4j.Slf4j;
import org.jboss.jandex.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

/**
 * IndexBuilder.
 *
 * @author Pierre Adam
 * @since 24.05.31
 */
@Slf4j
public class IndexBuilder {

    /**
     * The constant INDEX_PATH.
     */
    private static final String INDEX_PATH = "META-INF/jandex.idx";

    /**
     * The Index ur ls.
     */
    private final List<URL> indexURLs;

    /**
     * The Annotated types.
     */
    private final Set<Class<?>> annotatedTypes = new HashSet<>();

    /**
     * Creates a new instance of the index builder.
     *
     * @throws IOException in case of error checking for the Jandex index files
     */
    public IndexBuilder() throws IOException {
        this(IndexBuilder.INDEX_PATH);
    }

    /**
     * Instantiates a new Index builder.
     *
     * @param indexPaths the index paths
     * @throws IOException the io exception
     */
    IndexBuilder(final String... indexPaths) throws IOException {
        this.indexURLs = this.findIndexFiles(indexPaths);
        if (this.indexURLs.isEmpty()) {
            IndexBuilder.logger.error("No path selected !");
        }
    }

    /**
     * Dump index.
     *
     * @param index the index
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private static void dumpIndex(final Index index) throws UnsupportedEncodingException {
        final PrintStream oldStdout = System.out;
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final PrintStream newPS = new PrintStream(baos, true, Charset.defaultCharset().name())) {
            System.setOut(newPS);
            index.printAnnotations();
            index.printSubclasses();
            IndexBuilder.logger.debug(baos.toString(Charset.defaultCharset().name()));
        } finally {
            System.setOut(oldStdout);
        }
    }

    /**
     * Context class loader class loader.
     *
     * @return the class loader
     */
    private static ClassLoader contextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Resource name for class string.
     *
     * @param c the c
     * @return the string
     */
    private static String resourceNameForClass(final Class<?> c) {
        return c.getName().replace('.', '/') + ".class";
    }

//    /**
//     * Records each type that is annotated unless Jandex index(es) were found on
//     * the classpath (in which case we do not need to build our own in memory).
//     *
//     * @param <X>   annotated type
//     * @param event {@code ProcessAnnotatedType} event
//     */
//    private <X> void processAnnotatedType(@Observes final ProcessAnnotatedType<X> event) {
//        if (this.indexURLs.isEmpty()) {
//            final Class<?> c = event.getAnnotatedType()
//                    .getJavaClass();
//            this.annotatedTypes.add(c);
//        }
//    }

    /**
     * Reports an {@link IndexView} for the Jandex index that describes
     * annotated classes for endpoints.
     *
     * @return {@code IndexView} describing discovered classes
     * @throws IOException in case of error reading an existing index file or                     reading class bytecode from the classpath
     */
    public IndexView indexView() throws IOException {
        return !this.indexURLs.isEmpty() ? this.existingIndexFileReader() : this.indexFromHarvestedClasses();
    }

    /**
     * Builds an {@code IndexView} from existing Jandex index file(s) on the classpath.
     *
     * @return IndexView from all index files
     * @throws IOException in case of error attempting to open an index file
     */
    private IndexView existingIndexFileReader() throws IOException {
        final List<IndexView> indices = new ArrayList<>();
        for (final URL indexURL : this.indexURLs) {
            try (final InputStream indexIS = indexURL.openStream()) {
                IndexBuilder.logger.info("Adding Jandex index at {0}", indexURL);
                indices.add(new IndexReader(indexIS).read());
            } catch (final IOException ex) {
                throw new IOException("Attempted to read from previously-located index file "
                        + indexURL + " but the file cannot be found", ex);
            }
        }
        return indices.size() == 1 ? indices.get(0) : CompositeIndex.create(indices);
    }

    /**
     * Index from harvested classes index view.
     *
     * @return the index view
     * @throws IOException the io exception
     */
    private IndexView indexFromHarvestedClasses() throws IOException {
        final Indexer indexer = new Indexer();
        for (final Class<?> c : this.annotatedTypes) {
            try (final InputStream is = IndexBuilder.contextClassLoader().getResourceAsStream(IndexBuilder.resourceNameForClass(c))) {
                indexer.index(is);
            } catch (final IOException ex) {
                throw new IOException("Cannot load bytecode from class "
                        + c.getName() + " at " + IndexBuilder.resourceNameForClass(c)
                        + " for annotation processing", ex);
            }
        }

        IndexBuilder.logger.info("Using internal Jandex index created from CDI bean discovery");
        final Index result = indexer.complete();
        IndexBuilder.dumpIndex(result);
        return result;
    }

    /**
     * Find index files list.
     *
     * @param indexPaths the index paths
     * @return the list
     * @throws IOException the io exception
     */
    private List<URL> findIndexFiles(final String... indexPaths) throws IOException {
        final List<URL> result = new ArrayList<>();
        for (final String indexPath : indexPaths) {
            final Enumeration<URL> urls = IndexBuilder.contextClassLoader().getResources(indexPath);
            while (urls.hasMoreElements()) {
                result.add(urls.nextElement());
            }
        }
        return result;
    }
}
