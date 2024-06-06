package com.merim.digitalpayment.underflow.entities;

import com.merim.digitalpayment.underflow.app.Application;
import com.merim.digitalpayment.underflow.app.Mode;
import com.merim.digitalpayment.underflow.mdc.MDCContext;
import com.merim.digitalpayment.underflow.mdc.MDCKeys;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ServerError.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
@Schema(description = "This is a generic error class return by the server. Values can vary when used by an end developer. " +
        "Detail can be provided on the actual use of the error.")
public class ServerError {

    /**
     * The Request id.
     */
    @Schema(description = "Unique uid of the request. Is mostly used to match the requestUid with a potential error log from the application.",
            example = "880ff99b-a74d-46f0-8039-0c67931d2cb6")
    private final String requestUid;

    /**
     * The Type.
     */
    @Schema(description = "Describe the type of error.", example = "Not Found")
    private final String type;

    /**
     * The Message.
     */
    @Schema(description = "A more exhaustive description of the error.", example = "The resource is not found on the filesystem.")
    private final String message;

    /**
     * The Message.
     */
    @Schema(description = "A stacktrace or the reason of the error.", example = "Permission insufficient to open the resource.")
    private final String cause;

    /**
     * Instantiates a new Server error.
     *
     * @param type    the type
     * @param message the message
     * @param cause   the cause
     */
    public ServerError(final String type, final String message, final String cause) {
        this.requestUid = MDCContext.getInstance().getMDC(MDCKeys.Request.UID).orElse("Unavailable");
        this.type = type;
        this.message = message;
        this.cause = cause;
    }

    /**
     * Instantiates a new Server error.
     *
     * @param type    the type
     * @param message the message
     */
    public ServerError(final String type, final String message) {
        this(type, message, "");
    }

    /**
     * Instantiates a new Server error.
     *
     * @param type the type
     */
    public ServerError(final String type) {
        this(type, "", "");
    }

    /**
     * Instantiates a new Server error.
     *
     * @param type      the type
     * @param throwable the throwable
     */
    public ServerError(final String type, final Throwable throwable) {
        this.requestUid = MDCContext.getInstance().getMDC(MDCKeys.Request.UID).orElse("Unavailable");
        this.type = type;
        this.message = throwable.getMessage();

        if (Application.getMode() == Mode.PROD) {
            this.cause = "";
        } else {
            this.cause = Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
        }
    }

    /**
     * Gets request uid.
     *
     * @return the request uid
     */
    public String getRequestUid() {
        return this.requestUid;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Gets cause.
     *
     * @return the cause
     */
    public String getCause() {
        return this.cause;
    }
}
