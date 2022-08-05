package com.merim.digitalpayment.underflow.api.forms.errors;

/**
 * ServerError.
 *
 * @author Pierre Adam
 * @since 21.08.05
 */
public class ApiServerError {

    /**
     * The Request id.
     */
    private String requestUid;

    /**
     * The Type.
     */
    private String type;

    /**
     * The Message.
     */
    private String message;

    /**
     * The Message.
     */
    private String cause;

    /**
     * Instantiates a new Server error.
     */
    public ApiServerError() {
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
     * Sets request uid.
     *
     * @param requestUid the request uid
     */
    public void setRequestUid(final String requestUid) {
        this.requestUid = requestUid;
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
     * Sets type.
     *
     * @param type the type
     */
    public void setType(final String type) {
        this.type = type;
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
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * Gets cause.
     *
     * @return the cause
     */
    public String getCause() {
        return this.cause;
    }

    /**
     * Sets cause.
     *
     * @param cause the cause
     */
    public void setCause(final String cause) {
        this.cause = cause;
    }
}
