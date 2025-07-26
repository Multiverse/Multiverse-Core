package org.mvplugins.multiverse.core.utils.matcher;

import org.jetbrains.annotations.ApiStatus;

import java.util.regex.Pattern;

/**
 * WildcardStringMatcher is a StringMatcher that matches strings against a wildcard pattern.
 * It supports '*' as a wildcard character, which can match any sequence of characters.
 * <br />
 * For example, the wildcard "foo*bar" will match any string that starts with "foo" and ends with "bar",
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public class WildcardStringMatcher implements StringMatcher {

    private final String wildcard;
    private final Pattern pattern;

    /**
     * Creates a new WildcardStringMatcher with a wildcard string.
     * The wildcard string can contain '*' characters, which will be replaced with a regex equivalent.
     *
     * @param wildcard the wildcard string to match against.
     */
    @ApiStatus.AvailableSince("5.2")
    public WildcardStringMatcher(String wildcard) {
        this.wildcard = wildcard;
        this.pattern = Pattern.compile(("\\Q" + wildcard + "\\E").replace("*", "\\E.*\\Q"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String value) {
        return pattern.matcher(value).matches();
    }
}
