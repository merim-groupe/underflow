package com.merimdigitalmedia.underflow.path;

import com.merimdigitalmedia.underflow.annotation.routing.Path;

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
        NO_MATCH = new PathMatcher("", "notFound", false);
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
     * @param searchValue  the search value
     * @param ignoreCase   the ignore case
     */
    public PathMatcher(final String relativePath, final String searchValue, final boolean ignoreCase) {
        String search = searchValue;

        // Add / in front of the search because the slash after the previous search is not captured.
        if (!search.startsWith("/")) {
            search = "/" + search;
        }

        // If the relative path is empty, removing the leading / to allow for @Path("") to work.
        if (relativePath.isEmpty() && search.startsWith("/")) {
            search = search.substring(1);
        }

        final String format = String.format("^(?<pathCapture>%s)(?:/|$)", search);
        final int patternFlag = ignoreCase ? Pattern.CASE_INSENSITIVE : 0;
        final Pattern pattern = Pattern.compile(format, patternFlag);

        this.relativePath = relativePath;
        this.matcher = pattern.matcher(relativePath);
    }

    /**
     * Instantiates a new Path matcher.
     *
     * @param relativePath the relative path
     * @param path         the path
     */
    public PathMatcher(final String relativePath, final Path path) {
        this(relativePath, path.value(), path.ignoreCase());
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
        return this.relativePath.substring(this.matcher.group("pathCapture").length());
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

    /**
     * Reset the matcher.
     */
    public void reset() {
        this.matcher.reset();
    }
}
