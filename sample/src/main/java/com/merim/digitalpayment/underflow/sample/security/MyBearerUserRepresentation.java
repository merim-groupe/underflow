package com.merim.digitalpayment.underflow.sample.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * MyUserRepresentation.
 *
 * @param name   The Name.
 * @param scopes The Scopes.
 * @author Pierre Adam
 * @since 22.07.19
 */
public record MyBearerUserRepresentation(String name, List<String> scopes) {
    /**
     * Instantiates a new My user representation.
     *
     * @param name   the name
     * @param scopes the scopes
     */
    @JsonCreator
    public MyBearerUserRepresentation(
            @JsonProperty(value = "name", required = true) final String name,
            @JsonProperty(value = "scopes", required = true) final List<String> scopes) {
        this.name = name;
        this.scopes = scopes;
    }

    /**
     * Instantiates a new My user representation.
     *
     * @param name the name
     */
    public MyBearerUserRepresentation(final String name) {
        this(name, new ArrayList<>());
    }
}
