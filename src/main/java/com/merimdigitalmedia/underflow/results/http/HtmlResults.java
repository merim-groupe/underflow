package com.merimdigitalmedia.underflow.results.http;

import com.merimdigitalmedia.underflow.templates.TemplateSystem;
import com.merimdigitalmedia.underflow.templates.dto.DevErrorDTO;
import com.merimdigitalmedia.underflow.utils.Application;
import com.merimdigitalmedia.underflow.utils.Mode;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.undertow.io.IoCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * FlowAnswer.
 *
 * @author Pierre Adam
 * @since 22.07.12
 */
public interface HtmlResults {

    /**
     * End the request with a status 200 OK.
     *
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult ok(final Template template, final Object dataModel) {
        return this.result(200, template, dataModel);
    }

    /**
     * End the request with a status 200 OK.
     *
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult ok(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.result(200, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult created(final Template template, final Object dataModel) {
        return this.result(201, template, dataModel);
    }

    /**
     * End the request with a status 201 Created.
     *
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult created(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.result(201, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult badRequest(final Template template, final Object dataModel) {
        return this.result(400, template, dataModel);
    }

    /**
     * End the request with a status 400 Bad Request.
     *
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult badRequest(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.result(400, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult unauthorized(final Template template, final Object dataModel) {
        return this.result(401, template, dataModel);
    }

    /**
     * End the request with a status 401 Unauthorized.
     *
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult unauthorized(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.result(401, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult forbidden(final Template template, final Object dataModel) {
        return this.result(403, template, dataModel);
    }

    /**
     * End the request with a status 403 Forbidden.
     *
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult forbidden(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.result(403, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult notFound(final Template template, final Object dataModel) {
        return this.result(404, template, dataModel);
    }

    /**
     * End the request with a status 404 Not Found.
     *
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult notFound(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.result(404, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult internalServerError(final Template template, final Object dataModel) {
        return this.result(500, template, dataModel);
    }

    /**
     * End the request with a status 500 Internal Server Error.
     *
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult internalServerError(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.result(500, template, dataModel, ioCallback);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult serviceUnavailable(final Template template, final Object dataModel) {
        return this.result(503, template, dataModel);
    }

    /**
     * End the request with a status 503 Service Unavailable.
     *
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult serviceUnavailable(final Template template, final Object dataModel, final IoCallback ioCallback) {
        return this.result(503, template, dataModel, ioCallback);
    }

    /**
     * Ends the request with the given status.
     *
     * @param code      the code
     * @param template  the template
     * @param dataModel the data model
     * @return the result
     */
    default HttpResult result(final int code, final Template template, final Object dataModel) {
        return this.result(code, template, dataModel, IoCallback.END_EXCHANGE);
    }

    /**
     * HttpResult result.
     *
     * @param code       the code
     * @param template   the template
     * @param dataModel  the data model
     * @param ioCallback the io callback
     * @return the result
     */
    default HttpResult result(final int code, final Template template, final Object dataModel, final IoCallback ioCallback) {
        int finalCode = code;
        final Logger logger = LoggerFactory.getLogger(HtmlResults.class);
        final ByteArrayOutputStream renderOutput = new ByteArrayOutputStream();

        try {
            template.process(dataModel, new OutputStreamWriter(renderOutput));
        } catch (final TemplateException e) {
            logger.error("Render error.", e);
            try {
                final Template errorTemplate;
                finalCode = 500;
                renderOutput.reset();
                if (Application.getMode() == Mode.PROD) {
                    errorTemplate = TemplateSystem.getFrameworkTemplateEngine().getTemplate("error500.ftl");
                } else {
                    errorTemplate = TemplateSystem.getFrameworkTemplateEngine().getTemplate("dev-error.ftl");
                }
                errorTemplate.process(new DevErrorDTO(e), new OutputStreamWriter(renderOutput));
            } catch (final Exception e2) {
                throw new RuntimeException("Render of the framework error template failed !", e2);
            }
        } catch (final IOException e) {
            if (e.getCause() instanceof InterruptedIOException) {
                // Most likely end due to a network error.
                logger.warn("Rendering of a template has been interrupted.", e);
            } else {
                logger.error("An unexpected error occurred while rendering a template.", e);
            }
        }

        return new InputStreamHttpResult(finalCode, new ByteArrayInputStream(renderOutput.toByteArray()))
                .withContentType("text/html");
    }
}
