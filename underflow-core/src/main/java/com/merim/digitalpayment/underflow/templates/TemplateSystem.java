package com.merim.digitalpayment.underflow.templates;

/**
 * TemplateSystem.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
public class TemplateSystem {

    /**
     * The constant instance.
     */
    private static TemplateSystem instance;

    /**
     * The Framework templates.
     */
    private final TemplateEngine frameworkTemplates;

    /**
     * The Framework templates.
     */
    private TemplateEngine standardErrorsTemplates;

    /**
     * Instantiates a new Template tools.
     */
    private TemplateSystem() {
        this.frameworkTemplates = new TemplateEngine(TemplateSystem.class, "/_underflow_template");
        this.standardErrorsTemplates = this.frameworkTemplates;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    private static synchronized TemplateSystem getInstance() {
        if (TemplateSystem.instance == null) {
            TemplateSystem.instance = new TemplateSystem();
        }
        return TemplateSystem.instance;
    }

    /**
     * Gets template engine.
     *
     * @return the template engine
     */
    public static TemplateEngine getFrameworkTemplateEngine() {
        return TemplateSystem.getInstance().frameworkTemplates;
    }

    /**
     * Gets standard errors templates.
     *
     * @return the standard errors templates
     */
    public static TemplateEngine getStandardErrorsTemplates() {
        return TemplateSystem.getInstance().standardErrorsTemplates;
    }

    /**
     * Sets standard errors templates.
     *
     * @param standardErrorsTemplates the standard errors templates
     */
    public void setStandardErrorsTemplates(final TemplateEngine standardErrorsTemplates) {
        TemplateSystem.getInstance().standardErrorsTemplates = standardErrorsTemplates;
    }
}
