package org.mvplugins.multiverse.core.utils.matcher;

import com.dumptruckman.minecraft.util.Logging;
import io.vavr.control.Try;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * RegexStringMatcher is a StringMatcher that matches strings against a regex pattern.
 * It can be initialized with a regex string, which can optionally start with 'r=' to indicate it's a regex.
 *
 * @since 5.2
 */
@ApiStatus.AvailableSince("5.2")
public class RegexStringMatcher implements StringMatcher {
    private final @NotNull String regexString;
    private final @Nullable Pattern regexPattern;

    /**
     * Creates a new RegexStringMatcher with a regex string. 'r=' prefix will be stripped if present.
     *
     * @param regexString the regex string to match against. If it starts with 'r=', that part will be ignored.
     *
     * @since 5.2
     */
    @ApiStatus.AvailableSince("5.2")
    public RegexStringMatcher(@NotNull String regexString) {
        this.regexString = regexString;
        this.regexPattern = compileRegex(regexString);
    }

    private Pattern compileRegex(String regexString) {
        if (regexString.startsWith("r=")) {
            regexString = regexString.substring(2);
        }

        String finalRegexString = regexString;
        return Try.of(() -> Pattern.compile(finalRegexString))
                .onFailure(ex -> Logging.warning("Failed to compile regex '%s': %s",
                        finalRegexString, ex.getMessage()))
                .getOrNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matches(@Nullable String value) {
        if (regexPattern == null || value == null) {
            return false;
        }
        return regexPattern.matcher(value).matches();
    }
}
