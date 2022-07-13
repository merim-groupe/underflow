package com.merimdigitalmedia.underflow.handlers.flows;

import com.merimdigitalmedia.underflow.handlers.flows.answers.FlowHtmlAnswer;
import com.merimdigitalmedia.underflow.templates.TemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * FlowTemplateHandler.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public class FlowTemplateHandler extends FlowHandler implements FlowHtmlAnswer {

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
        this.templateEngine = new TemplateEngine(configuration);
    }

    /**
     * Instantiates a new Flow template handler.
     *
     * @param resourcePath the resource path
     */
    public FlowTemplateHandler(final String resourcePath) {
        this.templateEngine = new TemplateEngine(this.getClass(), resourcePath);
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
}
