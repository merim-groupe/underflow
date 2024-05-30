package com.merim.digitalpayment.underflow.handlers.context.path;

/**
 * PathMatcher.
 *
 * @author Pierre Adam
 * @since 24.05.30
 */
public interface PathMatcher {

    /**
     * Matches boolean.
     *
     * @param pattern the pattern
     * @return true if matches
     */
    boolean matches(String pattern);

    /**
     * Check if the matcher contains the given group.
     *
     * @param groupName the group name
     * @return the boolean
     */
    boolean hasGroup(final String groupName);

    /**
     * Gets the value matched in the given group.
     *
     * @param groupName the group name
     * @return the group
     */
    String getGroup(final String groupName);

    /**
     * The type Not matching path matcher.
     */
    class NotMatchingPathMatcher implements PathMatcher {

        @Override
        public boolean matches(final String pattern) {
            return false;
        }

        @Override
        public boolean hasGroup(final String groupName) {
            return false;
        }

        @Override
        public String getGroup(final String groupName) {
            return "";
        }
    }
}
