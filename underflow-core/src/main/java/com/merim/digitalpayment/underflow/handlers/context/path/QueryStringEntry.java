package com.merim.digitalpayment.underflow.handlers.context.path;

import jakarta.ws.rs.DefaultValue;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * QueryStringEntry.
 *
 * @author Pierre Adam
 * @since 24.06.04
 */
@Getter
public class QueryStringEntry {

    /**
     * The Data.
     */
    private final Deque<String> data;

    /**
     * The Required.
     */
    private final boolean required;

    /**
     * The default value.
     */
    private final DefaultValue defaultValue;

    /**
     * Instantiates a new Query string entry.
     *
     * @param required     the required
     * @param defaultValue the default value
     */
    public QueryStringEntry(final boolean required, final DefaultValue defaultValue) {
        this.data = new ArrayDeque<>();
        this.required = required;
        this.defaultValue = defaultValue;
    }

    /**
     * Gets data or default.
     *
     * @return the data or default
     */
    public Deque<String> getDataOrDefault() {
        if (!this.data.isEmpty()) {
            return this.data;
        } else {
            final Deque<String> defaultDeque = new ArrayDeque<>();

            if (this.defaultValue != null) {
                defaultDeque.add(this.defaultValue.value());
            }

            return defaultDeque;
        }
    }
}
