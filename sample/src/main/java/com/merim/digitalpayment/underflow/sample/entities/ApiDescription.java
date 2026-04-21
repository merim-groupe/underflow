package com.merim.digitalpayment.underflow.sample.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * ApiDescription.
 *
 * @param version The Version.
 * @param authors The Authors.
 * @author Pierre Adam
 * @since 22.07.20
 */
public record ApiDescription(String version, List<Author> authors) {

    /**
     * The constant TEST_INSTANCE.
     */
    public static final ApiDescription TEST_INSTANCE = new ApiDescription("22.08", new ArrayList<Author>() {{
        this.add(new Author("Pierre Adam", "p.adam@merim-groupe.com"));
        this.add(new Author("Lucas Stadelmann", "l.stadelmann@merim-groupe.com"));
    }});

    /**
     * Instantiates a new Api version.
     *
     * @param version the version
     * @param authors the authors
     */
    public ApiDescription {
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    @Override
    public String version() {
        return this.version;
    }

    /**
     * Gets authors.
     *
     * @return the authors
     */
    @Override
    public List<Author> authors() {
        return this.authors;
    }

    /**
     * The type Author.
     *
     * @param name  The Name.
     * @param email The Email.
     */
    public record Author(String name, String email) {
        /**
         * Instantiates a new Author.
         *
         * @param name  the name
         * @param email the email
         */
        public Author {
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        @Override
        public String name() {
            return this.name;
        }


        /**
         * Gets email.
         *
         * @return the email
         */
        @Override
        public String email() {
            return this.email;
        }
    }
}
