package com.merim.digitalpayment.underflow.handlers.context.path;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StandardPathMatcher.
 *
 * @author Pierre Adam
 * @since 24.05.30
 */
public class StandardPathMatcher implements PathMatcher {

    /**
     * The Path.
     */
    private final String queriedPath;

    /**
     * The Case sensitive.
     */
    private final boolean caseSensitive;

    /**
     * The Matcher.
     */
    private Matcher matcher;

    /**
     * Instantiates a new Regex path matcher.
     *
     * @param queriedPath   the path
     * @param caseSensitive the case sensitive
     */
    public StandardPathMatcher(final String queriedPath,
                               final boolean caseSensitive) {
        this.queriedPath = queriedPath;
        this.caseSensitive = caseSensitive;
        this.matcher = null;
    }

    @Override
    public boolean matches(final String pathPattern) {
        final String format = String.format("^(?<pathCapture>%s)$", pathPattern);
        final int patternFlag = this.caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        final Pattern pattern = Pattern.compile(format, patternFlag);

        this.matcher = pattern.matcher(this.queriedPath);
        return this.matcher.find();
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
