package com.merimdigitalmedia.underflow.templates.dto;

import freemarker.template.TemplateException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DevErrorDTO.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
public class DevErrorDTO {

    /**
     * The Title.
     */
    private String title;

    /**
     * The Message.
     */
    private String message;

    /**
     * The Location.
     */
    private String location;

    /**
     * The Detail.
     */
    private List<String> detail;

    /**
     * Instantiates a new Error dto.
     */
    public DevErrorDTO() {
        this.title = "";
        this.message = "";
        this.location = "";
        this.detail = new ArrayList<>();
    }

    /**
     * Instantiates a new Error dto.
     *
     * @param title   the title
     * @param message the message
     */
    public DevErrorDTO(final String title, final String message) {
        this();
        this.title = title;
        this.message = message;
    }

    /**
     * Instantiates a new Error dto.
     *
     * @param e the exception
     */
    public DevErrorDTO(final TemplateException e) {
        this.title = "Template rendering error";
        this.message = e.getFTLInstructionStack();
        this.location = e.getTemplateSourceName();
        this.detail = Arrays.asList(e.getMessage().split("\n"));
    }

    /**
     * Instantiates a new Error dto.
     *
     * @param e the exception
     */
    public DevErrorDTO(final Throwable e) {
        this.title = "Exception";
        this.message = e.getMessage();
        this.location = e.getClass().getCanonicalName();
        this.detail = new ArrayList<>();

        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            this.detail.add(cause.getMessage() == null ? "" : cause.getMessage());
            this.detail.addAll(Arrays.stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .collect(Collectors.toList()));
            if (cause.getCause() != null) {
                this.detail.add("--  caused by  --");
            }
        }
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle() {
        return this.title == null ? "" : this.title;
    }

    /**
     * Sets title.
     *
     * @param title the title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return this.message == null ? "" : this.message;
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
     * Gets location.
     *
     * @return the location
     */
    public String getLocation() {
        return this.location == null ? "" : this.location;
    }

    /**
     * Sets location.
     *
     * @param location the location
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * Gets detail.
     *
     * @return the detail
     */
    public List<String> getDetail() {
        return this.detail == null ? new ArrayList<>() : this.detail;
    }

    /**
     * Sets detail.
     *
     * @param detail the detail
     */
    public void setDetail(final List<String> detail) {
        this.detail = detail.stream().map(s -> s == null ? "" : s).collect(Collectors.toList());
    }
}
