package com.onarandombox.MultiverseCore.displaytools;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Filter content and text based on regex.
 */
public class ContentFilter {

    public static final ContentFilter DEFAULT = new ContentFilter();
    private static final Pattern REGEX_SPECIAL_CHARS = Pattern.compile("[.+*?\\[^\\]$(){}=!<>|:-\\\\]");

    public static ContentFilter getDefault() {
        return DEFAULT;
    }

    private String filterString;
    private Pattern filterPattern;
    private boolean exactMatch;

    public ContentFilter() {
    }

    public ContentFilter(@NotNull String filterString) {
        this(filterString, false);
    }

    public ContentFilter(@NotNull String filterString,
                         boolean exactMatch) {

        this.filterString = filterString;
        this.exactMatch = exactMatch;
        parseFilter();
    }

    /**
     * Compile regex pattern based on {@link ContentFilter#filterString}.
     * When prefixed with 'r=', use {@link ContentFilter#filterString} as the full regex pattern.
     * Else, set to any match that contains the {@link ContentFilter#filterString}.
     */
    private void parseFilter() {
        if (filterString == null) {
            return;
        }
        if (filterString.startsWith("r=")) {
            convertToMatcher(filterString.substring(2));
            return;
        }
        String cleanedFilter = REGEX_SPECIAL_CHARS.matcher(filterString.toLowerCase()).replaceAll("\\\\$0");
        convertToMatcher("(?i).*" + cleanedFilter + ".*");
    }

    /**
     * Compile and store the regex into a {@link Pattern}.
     */
    private void convertToMatcher(@NotNull String regex) {
        try {
            this.filterPattern = Pattern.compile(regex);
            Logging.finest("Parsed regex pattern: %s", this.filterPattern.toString());
        }
        catch (PatternSyntaxException ignored) {
            Logging.warning("Error parsing regex: %s", filterString);
        }
    }

    /**
     * Do regex matching.
     *
     * @param text String to check regex on.
     * @return True of matches regex pattern, false otherwise.
     */
    public boolean checkMatch(@Nullable Object text) {
        if (!hasFilter()) {
            return true;
        }
        if (text == null || !hasValidPattern()) {
            return false;
        }
        text = ChatColor.stripColor(String.valueOf(text));
        return (exactMatch)
                ? filterPattern.matcher((CharSequence) text).matches()
                : filterPattern.matcher((CharSequence) text).find();
    }

    public boolean hasFilter() {
        return filterString != null;
    }

    public boolean hasValidPattern() {
        return filterPattern != null;
    }

    public @Nullable String getString() {
        return filterString;
    }

    public @Nullable Pattern getPattern() {
        return filterPattern;
    }

    public boolean isExactMatch() {
        return exactMatch;
    }

    /**
     * Nicely format the filter string to be used for showing the sender.
     *
     * @return formatted filter string.
     */
    public @NotNull String getFormattedString() {
        return String.format("%sFilter: '%s'%s", ChatColor.ITALIC, filterString, ChatColor.RESET);
    }

    @Override
    public String toString() {
        return "ContentFilter{" +
                "filterString='" + filterString + '\'' +
                ", filterPattern=" + filterPattern +
                ", exactMatch=" + exactMatch +
                '}';
    }
}