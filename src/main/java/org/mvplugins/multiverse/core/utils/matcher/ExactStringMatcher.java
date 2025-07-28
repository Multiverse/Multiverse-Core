package org.mvplugins.multiverse.core.utils.matcher;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * ExactStringMatcher is a StringMatcher that matches strings against a set of exact values.
 * It can be initialized with a single string, a collection of strings, or can have exact matches added later.
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public class ExactStringMatcher implements StringMatcher {
    private final Set<String> exactMatches;

    /**
     * Creates a new ExactStringMatcher with no initial matches. Use {@link #addExactMatch(String)} to add matches later.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public ExactStringMatcher() {
        this.exactMatches = new HashSet<>();
    }

    /**
     * Creates a new ExactStringMatcher with a single exact match.
     *
     * @param exactMatch the exact string to match against
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public ExactStringMatcher(String exactMatch) {
        this.exactMatches = new HashSet<>();
        this.exactMatches.add(exactMatch);
    }

    /**
     * Creates a new ExactStringMatcher with multiple exact matches.
     *
     * @param exactMatches the collection of exact strings to match against
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public ExactStringMatcher(Collection<String> exactMatches) {
        this.exactMatches = new HashSet<>(exactMatches);
    }

    /**
     * Adds an exact match string to this matcher.
     *
     * @param value the exact string to add to the matcher
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public void addExactMatch(String value) {
        this.exactMatches.add(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String value) {
        return exactMatches.contains(value);
    }
}
