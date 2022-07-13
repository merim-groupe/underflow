package com.merimdigitalmedia.underflow.templates;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.IOException;

/**
 * FreemarkerHelper.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public class TemplateEngine {

    /**
     * The Configuration.
     */
    private final Configuration configuration;

    /**
     * Instantiates with a configuration.
     *
     * @param configuration the configuration
     */
    public TemplateEngine(final Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Instantiates with a resource path.
     * The path should start with a / except if you know what you're doing.
     * Example:
     * TemplateEngine.init(this.getClass, "/templates");
     * will reference a folder "templates" in the resources of your jar of your class.
     *
     * @param aClass       the class
     * @param resourcePath the resource path
     */
    public TemplateEngine(final Class<?> aClass, final String resourcePath) {
        this(new Configuration(Configuration.VERSION_2_3_31));

        this.configuration.setClassForTemplateLoading(aClass, resourcePath);
        this.configuration.setDefaultEncoding("UTF-8");
        this.configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        this.configuration.setLogTemplateExceptions(false);
        this.configuration.setWrapUncheckedExceptions(true);
        this.configuration.setFallbackOnNullLoopVariable(false);
    }

    /**
     * Gets configuration.
     *
     * @return the configuration
     */
    public Configuration getConfiguration() {
        return this.configuration;
    }

    /**
     * Gets template.
     *
     * @param path the path
     * @return the template
     */
    public Template getTemplate(final String path) {
        try {
            return this.configuration.getTemplate(path);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
