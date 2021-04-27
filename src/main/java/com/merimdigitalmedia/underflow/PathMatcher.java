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

    /**
     * The Relative path.
     */
    private final String relativePath;

    /**
     * The Matcher.
     */
    private final Matcher matcher;

    static {
        NO_MATCH = new PathMatcher("", Pattern.compile("notFound"));
    }

    /**
     * Instantiates a new Path matcher.
     *
     * @param relativePath the relative path
     * @param pattern      the pattern
     */
    public PathMatcher(final String relativePath, final Pattern pattern) {
        this.relativePath = relativePath;
        this.matcher = pattern.matcher(relativePath);
    }

    /**
     * No match path matcher.
     *
     * @return the path matcher
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
     * Gets match.
     *
     * @return the match
     */
    public String getMatch() {
        return this.matcher.group();
    }

    /**
     * Gets remaining.
     *
     * @return the remaining
     */
    public String getRemaining() {
        return this.relativePath.substring(this.matcher.end());
    }

    /**
     * Gets group.
     *
     * @param name the name
     * @return the group
     */
    public String getGroup(final String name) {
        return this.matcher.group(name);
    }
}
