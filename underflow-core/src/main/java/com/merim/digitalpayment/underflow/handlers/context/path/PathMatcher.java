package com.merim.digitalpayment.underflow.handlers.context.path;

import com.merim.digitalpayment.underflow.annotation.routing.Path;
import com.merim.digitalpayment.underflow.annotation.routing.PathPrefix;

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
        this(relativePath, null, searchValue, ignoreCase);
    }

    /**
     * Instantiates a new Path matcher.
     *
     * @param relativePath the relative path
     * @param searchPrefix the search prefix
     * @param searchValue  the search value
     * @param ignoreCase   the ignore case
     */
    public PathMatcher(final String relativePath, final String searchPrefix, final String searchValue, final boolean ignoreCase) {
        String search = "";

        if (searchPrefix != null && !searchPrefix.isEmpty()) {
            search = searchPrefix;
            // Add / in front of the search because the slash after the previous search is not captured.
            if (!search.startsWith("/")) {
                search = "/" + search;
            }
            // Remove tailing / to avoid double /.
            if (search.endsWith("/")) {
                search = search.substring(search.length() - 1);
            }
        }

        // Only add the search value if it is not an "empty path". This allows @Path("") and @Path("/")
        if (!searchValue.isEmpty() && !searchValue.equals("/")) {
            // Add / in front of the search because the slash after the previous search is not captured.
            if (!searchValue.startsWith("/")) {
                search += "/";
            }
            search += searchValue;
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
     * @param pathPrefix   the path prefix
     * @param path         the path
     */
    public PathMatcher(final String relativePath, final PathPrefix pathPrefix, final Path path) {
        this(relativePath, pathPrefix == null ? null : pathPrefix.value(), path.value(), path.ignoreCase());
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
        final String match = this.matcher.group().replaceAll("/$", "");
        return match.isEmpty() ? "/" : match;
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
            final String group = this.matcher.group(groupName);
            return group != null;
        } catch (final Exception ignore) {
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
