package com.merimdigitalmedia.underflow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PathMatcher.
 *
 * @author Pierre Adam
 * @since 21.04.27
 */
public class PathMatcher {

    /**
     * The constant NO_MATCH.
     */
    private static final PathMatcher NO_MATCH;

    static {
        NO_MATCH = new PathMatcher("", Pattern.compile("notFound"));
    }

    /**
     * The Relative path.
     */
    private final String relativePath;

    /**
     * The Matcher.
     */
    private final Matcher matcher;

    /**
     * Instantiates a new Path matcher.
     *
     * @param relativePath the relative path
     * @param pattern      the pattern
     */
    public PathMatcher(final String relativePath,
                       final Pattern pattern) {
        this.relativePath = relativePath;
        this.matcher = pattern.matcher(relativePath);
    }

    /**
     * Gets no match instance.
     *
     * @return the no match path matcher
     */
    public static PathMatcher noMatch() {
        return PathMatcher.NO_MATCH;
    }

    /**
     * Matches boolean.
     *
     * @return true if matches
     */
    public boolean find() {
        return this.matcher.find();
    }

    /**
     * Gets the full match.
     *
     * @return the match
     */
    public String getFullMatch() {
        return this.matcher.group();
    }

    /**
     * Gets the remaining path.
     *
     * @return the remaining path
     */
    public String getRemainingPath() {
        return this.relativePath.substring(this.matcher.end());
    }

    /**
     * Check if the matcher contains the given group.
     *
     * @param groupName the group name
     * @return the boolean
     */
    public boolean hasGroup(final String groupName) {
        try {
            this.matcher.group(groupName);
            return true;
        } catch (final IllegalArgumentException ignore) {
            return false;
        }
    }

    /**
     * Gets the value matched in the given group.
     *
     * @param groupName the group name
     * @return the group
     */
    public String getGroup(final String groupName) {
        return this.matcher.group(groupName);
    }
}
