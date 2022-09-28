package com.merim.digitalpayment.underflow.tests.sample.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * ApiDescription.
 *
 * @author Pierre Adam
 * @since 22.07.20
 */
public class ApiDescription {

    /**
     * The constant TEST_INSTANCE.
     */
    public static final ApiDescription TEST_INSTANCE = new ApiDescription("22.08", new ArrayList<Author>() {{
        this.add(new Author("Pierre Adam", "p.adam@merim-groupe.com"));
        this.add(new Author("Lucas Stadelmann", "l.stadelmann@merim-groupe.com"));
    }});

    /**
     * The Version.
     */
    private final String version;

    /**
     * The Authors.
     */
    private final List<Author> authors;

    /**
     * Instantiates a new Api version.
     *
     * @param version the version
     * @param authors the authors
     */
    public ApiDescription(final String version, final List<Author> authors) {
        this.version = version;
        this.authors = authors;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Gets authors.
     *
     * @return the authors
     */
    public List<Author> getAuthors() {
        return this.authors;
    }

    /**
     * The type Author.
     */
    public static class Author {
        /**
         * The Name.
         */
        private final String name;

        /**
         * The Email.
         */
        private final String email;

        /**
         * Instantiates a new Author.
         *
         * @param name  the name
         * @param email the email
         */
        public Author(final String name, final String email) {
            this.name = name;
            this.email = email;
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return this.name;
        }


        /**
         * Gets email.
         *
         * @return the email
         */
        public String getEmail() {
            return this.email;
        }
    }
}
