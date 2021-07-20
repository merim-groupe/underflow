package com.merimdigitalmedia.underflow.path;

import java.util.ArrayList;
import java.util.List;

/**
 * PathMatcherBundle.
 *
 * @author Pierre Adam
 * @since 21.07.20
 */
public class PathMatcherBundle {

    /**
     * The Path matchers.
     */
    private final List<PathMatcher> pathMatchers;

    /**
     * Instantiates a new Path matcher bundle.
     */
    public PathMatcherBundle() {
        this.pathMatchers = new ArrayList<>();
    }

    /**
     * Add matcher.
     *
     * @param pathMatcher the path matcher
     */
    public void addMatcher(final PathMatcher pathMatcher) {
        this.pathMatchers.add(pathMatcher);
    }

    /**
     * Find a fitting path matcher.
     *
     * @return the path matcher
     */
    public PathMatcher find() {
        for (final PathMatcher pathMatcher : this.pathMatchers) {
            if (pathMatcher.find()) {
                pathMatcher.reset();
                return pathMatcher;
            }
        }

        return PathMatcher.noMatch();
    }
}
