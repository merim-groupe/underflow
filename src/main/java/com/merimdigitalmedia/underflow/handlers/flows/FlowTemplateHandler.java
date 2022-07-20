package com.merimdigitalmedia.underflow.handlers.flows;

import com.merimdigitalmedia.underflow.results.Result;
import com.merimdigitalmedia.underflow.results.http.HtmlResults;
import com.merimdigitalmedia.underflow.security.FlowSecurity;
import com.merimdigitalmedia.underflow.templates.TemplateEngine;
import com.merimdigitalmedia.underflow.templates.TemplateSystem;
import com.merimdigitalmedia.underflow.templates.dto.DevErrorDTO;
import com.merimdigitalmedia.underflow.utils.Application;
import com.merimdigitalmedia.underflow.utils.Mode;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.undertow.server.HttpServerExchange;

import java.io.File;
import java.io.IOException;

/**
 * FlowTemplateHandler.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public class FlowTemplateHandler extends FlowHandler implements HtmlResults {

    /**
     * The Template engine.
     */
    final private TemplateEngine templateEngine;

    /**
     * Instantiates a new Flow template handler.
     *
     * @param configuration the configuration
     */
    public FlowTemplateHandler(final Configuration configuration) {
        this(configuration, null);
    }

    /**
     * Instantiates a new Flow template handler.
     *
     * @param configuration the configuration
     */
    public FlowTemplateHandler(final Configuration configuration, final FlowSecurity<?, ?> flowSecurity) {
        super(flowSecurity);
        this.templateEngine = new TemplateEngine(configuration);
    }

    /**
     * Instantiates a new Flow template handler.
     *
     * @param resourcePath the resource path
     */
    public FlowTemplateHandler(final String resourcePath) {
        this(resourcePath, null);
    }

    /**
     * Instantiates a new Flow template handler.
     *
     * @param resourcePath the resource path
     */
    public FlowTemplateHandler(final String resourcePath, final FlowSecurity<?, ?> flowSecurity) {
        super(flowSecurity);
        this.templateEngine = new TemplateEngine(this.getClass(), resourcePath);
    }

    /**
     * Instantiates a new Flow template handler.
     *
     * @param dirPath the dir path
     */
    public FlowTemplateHandler(final File dirPath) {
        this(dirPath, null);
    }

    /**
     * Instantiates a new Flow template handler.
     *
     * @param dirPath the dir path
     */
    public FlowTemplateHandler(final File dirPath, final FlowSecurity<?, ?> flowSecurity) {
        super(flowSecurity);
        try {
            this.templateEngine = new TemplateEngine(dirPath);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets template.
     *
     * @param path the path
     * @return the template
     */
    protected Template getTemplate(final String path) {
        return this.templateEngine.getTemplate(path);
    }

    @Override
    public Result onNotFound(final HttpServerExchange exchange) {
        return this.notFound(TemplateSystem.getStandardErrorsTemplates().getTemplate("error404.ftl"), null);
    }

    @Override
    public Result onException(final HttpServerExchange exchange, final Throwable exception) {
        if (Application.getMode() == Mode.PROD) {
            return this.internalServerError(TemplateSystem.getStandardErrorsTemplates().getTemplate("error500.ftl"), null);
        } else {
            return this.internalServerError(TemplateSystem.getFrameworkTemplateEngine().getTemplate("dev-error.ftl"), new DevErrorDTO(exception));
        }
    }
}
