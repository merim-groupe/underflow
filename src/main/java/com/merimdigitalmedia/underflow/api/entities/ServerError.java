package com.merimdigitalmedia.underflow.api.entities;

import com.merimdigitalmedia.underflow.mdc.MDCContext;
import com.merimdigitalmedia.underflow.mdc.MDCKeys;

/**
 * ServerError.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
public class ServerError {

    /**
     * The Request id.
     */
    private final String requestUid;

    /**
     * The Type.
     */
    private final String type;

    /**
     * The Message.
     */
    private final String message;

    /**
     * The Message.
     */
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