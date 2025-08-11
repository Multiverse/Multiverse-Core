package org.mvplugins.multiverse.core.utils.matcher;

import com.dumptruckman.minecraft.util.Logging;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * MatcherGroup is a collection of StringMatchers that can match against a string.
 * It works with all format supported by {@link StringMatcher#fromString(String)}.
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public class MatcherGroup implements StringMatcher {

    private final ExactStringMatcher exactMatcher;
    private final List<StringMatcher> stringMatchers;

    /**
     * Creates a new empty MatcherGroup.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public MatcherGroup() {
        this.exactMatcher = new ExactStringMatcher();
        this.stringMatchers = new ArrayList<>();
    }

    /**
     * Creates a new MatcherGroup with multiple strings to be parsed into matchers.
     *
     * @param matchStrings the collection of match strings
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public MatcherGroup(@NotNull Collection<String> matchStrings) {
        this();
        for (String matchString : matchStrings) {
            addMatcher(matchString);
        }
    }

    /**
     * Creates a new MatcherGroup with a single exact match.
     *
     * @param matchString the single match string
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public void addMatcher(@Nullable String matchString) {
        if (matchString == null || matchString.isEmpty()) {
            return;
        }
        if (isExact(matchString)) {
            exactMatcher.addExactMatch(matchString);
        } else {
            stringMatchers.add(StringMatcher.fromString(matchString));
        }
    }

    private boolean isExact(@NotNull String matcherString) {
        return !matcherString.contains("*") && !matcherString.startsWith("r=");
    }

    /**
     * Adds an existing matcher to the group.
     *
     * @param matcher the StringMatcher to add
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public void addMatcher(@NotNull StringMatcher matcher) {
        stringMatchers.add(matcher);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@Nullable String value) {
        if (exactMatcher.matches(value)) {
            return true;
        }
        for (StringMatcher matcher : stringMatchers) {
            if (matcher.matches(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "MatcherGroup{" + "exactMatcher=" + exactMatcher +
                ", stringMatchers=" + stringMatchers +
                '}';
    }
}
