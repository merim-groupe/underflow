package com.merim.digitalpayment.underflow.tests.sample.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.merim.digitalpayment.underflow.security.JwtUserRepresentation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * MyUserRepresentation.
 *
 * @author Pierre Adam
 * @since 22.07.19
 */
@NoArgsConstructor
@Getter
public class MyUserRepresentation extends JwtUserRepresentation<MyUserRepresentation.UserData> {

    /**
     * Instantiates a new My user representation.
     *
     * @param name   the name
     * @param scopes the scopes
     */
    public MyUserRepresentation(final String name, final List<String> scopes) {
        this.setData(new UserData(name, scopes));

        // Set the standard JWT claims
        this.setSubject(name);
        this.setAudience(Set.of(MySecurity.ISSUER)); // Set the audience to ourselves.
        this.setJwtId(UUID.randomUUID().toString());
        this.setExpiration(Date.from(Instant.now().plus(Duration.ofHours(1))));
    }

    /**
     * The type User data.
     */
    @Getter
    @Setter
    public static class UserData {

        /**
         * The Name.
         */
        private String name;

        /**
         * The Scopes.
         */
        private List<String> scopes;

        /**
         * Instantiates a new My user representation.
         *
         * @param name   the name
         * @param scopes the scopes
         */
        @JsonCreator
        public UserData(
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
        public UserData(final String name) {
            this(name, new ArrayList<>());
        }
    }
}
