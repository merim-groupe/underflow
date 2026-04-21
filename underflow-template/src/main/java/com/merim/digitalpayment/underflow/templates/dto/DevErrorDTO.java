package com.merim.digitalpayment.underflow.templates.dto;

import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DevErrorDTO.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
@Getter
@Setter
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
                    .toList());
            if (cause.getCause() != null) {
                this.detail.add("--  caused by  --");
            }
        }
    }
}
