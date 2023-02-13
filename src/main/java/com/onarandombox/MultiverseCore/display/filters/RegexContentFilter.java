package com.onarandombox.MultiverseCore.display.filters;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.dumptruckman.minecraft.util.Logging;
import com.google.common.base.Strings;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Filter content and text based on regex matching.
 */
public class RegexContentFilter implements ContentFilter {

    private static final Pattern REGEX_SPECIAL_CHARS = Pattern.compile("[.+*?\\[^\\]$(){}=!<>|:-\\\\]");

    /**
     * Compile regex pattern to create a regex filter.
     *
     * When prefixed with 'r=', filter string is used as the full regex pattern.
     * Else, set to regex that contains the filterString.
     *
     * @param filterString  The target string to create filter.
     * @return A new instance of {@link RegexContentFilter} with filter applied.
     */
    @NotNull
    public static RegexContentFilter fromString(@Nullable String filterString) {
        if (filterString == null) {
            return new RegexContentFilter(null);
        }
        if (filterString.startsWith("r=")) {
            return new RegexContentFilter(filterString.substring(2));
        }
        String cleanedFilter = REGEX_SPECIAL_CHARS.matcher(filterString.toLowerCase()).replaceAll("\\\\$0");
        return new RegexContentFilter(cleanedFilter);
    }

    private final String regexString;
    private Pattern regexPattern;

    public RegexContentFilter(@Nullable String regexString) {
        this.regexString = regexString;
        convertToPattern();
    }

    /**
     * Try to compile and store the regex into a {@link Pattern}.
     */
    private void convertToPattern() {
        if (Strings.isNullOrEmpty(regexString)) {
            return;
        }
        try {
            regexPattern = Pattern.compile(regexString);
        } catch (PatternSyntaxException ignored) {
            regexPattern = null;
            Logging.fine("Error parsing regex: %s", regexString);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkMatch(String value) {
        if (!hasValidRegex()) {
            return false;
        }
        String text = ChatColor.stripColor(String.valueOf(value));
        return regexPattern.matcher(text).find();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean needToFilter() {
        return hasValidRegex();
    }

    public boolean hasValidRegex() {
        return regexPattern != null;
    }

    public String getRegexString() {
        return regexString;
    }

    public Pattern getRegexPattern() {
        return regexPattern;
    }

    @Override
    public String toString() {
        return regexString;
    }
}
