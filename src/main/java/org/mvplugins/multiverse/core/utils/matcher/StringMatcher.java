package org.mvplugins.multiverse.core.utils.matcher;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * StringMatcher is an interface for matching strings against various patterns.
 * It currently built-in supports exact matches, wildcard matches, and regex matches.
 * <br />
 * Implement this interface to create your own custom string matchers.
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public interface StringMatcher {

    /**
     * Creates a StringMatcher from a string representation. When the string starts with "r=", it is treated as a regex.
     * If the string contains a '*', it is treated as a wildcard match.
     *
     * @param matcherString the string to be parsed into a matcher.
     * @return a StringMatcher instance based on the provided string.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    static @NotNull StringMatcher fromString(@NotNull String matcherString) {
        if (matcherString.startsWith("r=")) {
            return new RegexStringMatcher(matcherString);
        } else if (matcherString.contains("*")) {
            return new WildcardStringMatcher(matcherString);
        } else {
            return new ExactStringMatcher(matcherString);
        }
    }

    /**
     * Checks if the given value matches the pattern defined by this StringMatcher.
     *
     * @param value the string to match against the pattern.
     * @return true if the value matches, false otherwise.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    boolean matches(@Nullable String value);

    /**
     * Filters a list of strings, returning only those that match the pattern defined by this StringMatcher.
     * This method is a convenience for applying the matcher to a collection of strings.
     *
     * @param values the list of strings to filter.
     * @return A list of strings that match the pattern defined by this StringMatcher.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    default @NotNull List<String> filter(@NotNull List<String> values) {
        return values.stream()
                .filter(this::matches)
                .collect(Collectors.toList());
    }
}
