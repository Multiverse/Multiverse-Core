package org.mvplugins.multiverse.core.utils.matcher;

import org.jetbrains.annotations.ApiStatus;

import java.util.regex.Pattern;

/**
 * RegexStringMatcher is a StringMatcher that matches strings against a regex pattern.
 * It can be initialized with a regex string, which can optionally start with 'r=' to indicate it's a regex.
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public class RegexStringMatcher implements StringMatcher {
    private final String regexString;
    private final Pattern regexPattern;

    /**
     * Creates a new RegexStringMatcher with a regex string. 'r=' prefix will be stripped if present.
     *
     * @param regexString the regex string to match against. If it starts with 'r=', that part will be ignored.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public RegexStringMatcher(String regexString) {
        this.regexString = regexString;
        this.regexPattern = compileRegex(regexString);
    }

    private Pattern compileRegex(String regexString) {
        if (regexString.startsWith("r=")) {
            regexString = regexString.substring(2);
        }
        return Pattern.compile(regexString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(String value) {
        return regexPattern.matcher(value).matches();
    }
}
