package com.merim.digitalpayment.underflow.tests.sample.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * MyUserRepresentation.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
@Getter
public class MyUserRepresentation {

    /**
     * The Name.
     */
    private final String name;

    /**
     * The Scopes.
     */
    private final List<String> scopes;

    /**
     * Instantiates a new My user representation.
     *
     * @param name   the name
     * @param scopes the scopes
     */
    @JsonCreator
    public MyUserRepresentation(
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
    public MyUserRepresentation(final String name) {
        this(name, new ArrayList<>());
    }
}
