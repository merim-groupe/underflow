package com.merim.digitalpayment.underflow.i18n.messageformat;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AdvancedMessageFormat.
 *
 * @author Pierre Adam
 * @since 25.08.04
 */
public class AdvancedMessageFormat {

    /**
     * The constant REGEX_PATTERN.
     */
    private static final Pattern REGEX_PATTERN = Pattern.compile("(?<!')\\{(?<name>[^},]+)(?<args>(?:,[^},]+)*)}(?!')");

    /**
     * The Args.
     */
    private final List<Object> args;

    /**
     * The Idx.
     */
    private int idx;

    /**
     * The Pattern.
     */
    private String pattern;

    /**
     * Instantiates a new Advanced message format.
     *
     * @param pattern the pattern
     */
    private AdvancedMessageFormat(final String pattern) {
        this.idx = 0;
        this.pattern = pattern;
        this.args = new ArrayList<>();
    }

    /**
     * Format string.
     *
     * @param pattern the pattern
     * @param args    the args
     * @return the string
     */
    public static String format(final String pattern, final Object... args) {
        final MessageFormat formatter = new MessageFormat(pattern);
        return formatter.format(args);
    }

    /**
     * Format string.
     *
     * @param pattern the pattern
     * @param args    the args
     * @return the string
     */
    public static String format(final String pattern, final Map<String, Object> args) {
        final AdvancedMessageFormat formatter = new AdvancedMessageFormat(pattern);

        formatter.resolveArgs(s -> Optional.ofNullable(args.get(s)));

        return formatter.format();
    }

    /**
     * Format string.
     *
     * @return the string
     */
    private String format() {
        final MessageFormat formatter = new MessageFormat(this.pattern);

        return formatter.format(this.args.toArray());
    }

    /**
     * Resolve args.
     *
     * @param resolver the resolver
     */
    private void resolveArgs(final Function<String, Optional<Object>> resolver) {
        final Matcher matcher = AdvancedMessageFormat.REGEX_PATTERN.matcher(this.pattern);

        while (matcher.find()) {
            final String name = matcher.group("name");
            final String args = matcher.group("args");

            final Optional<Object> argValue = resolver.apply(name);

            if (argValue.isPresent()) {
                this.pattern = this.pattern.replace(matcher.group(), "{" + this.idx + (args != null ? args : "") + "}");
                this.idx++;

                this.args.add(argValue.get());
            } else {
                this.pattern = this.pattern.replace(matcher.group(), "'{'" + name + (args != null ? args : "") + "'}'");
            }
        }
    }
}
