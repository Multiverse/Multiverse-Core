package com.onarandombox.MultiverseCore.displaytools;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p>Filter content and text based on regex matching.</p>
 *
 * <p>Compile regex pattern based on {@link ContentFilter#filterString}. When prefixed with 'r=',
 * use {@link ContentFilter#filterString} as the full regex pattern. Else, set to any match that
 * contains the {@link ContentFilter#filterString}.<p>
 */
public class ContentFilter {

    public static final ContentFilter DEFAULT = new ContentFilter();
    private static final Pattern REGEX_SPECIAL_CHARS = Pattern.compile("[.+*?\\[^\\]$(){}=!<>|:-\\\\]");

    private String filterString;
    private Pattern filterPattern;
    private boolean exactMatch;

    private ContentFilter() {
    }

    /**
     * @param filterString  The text to do matching, either plaintext or regex.
     */
    public ContentFilter(@NotNull String filterString) {
        this(filterString, false);
    }

    /**
     * @param filterString  The text to do matching, else plaintext or regex.
     * @param exactMatch    Should check for exact match when doing regex matching.
     */
    public ContentFilter(@NotNull String filterString,
                         boolean exactMatch) {

        this.filterString = filterString;
        this.exactMatch = exactMatch;
        parseFilter();
    }

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
     *
     * @param regex The regex text.
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

    /**
     * Checks if a filter string is present.
     *
     * @return True if there is a filter string, else false.
     */
    public boolean hasFilter() {
        return filterString != null;
    }

    /**
     * Checks if regex pattern syntax is valid.
     *
     * @return True if valid, else false.
     */
    public boolean hasValidPattern() {
        return filterPattern != null;
    }

    /**
     * @return The filter string.
     */
    @Nullable
    public String getString() {
        return filterString;
    }

    /**
     * @return The regex pattern.
     */
    @Nullable
    public Pattern getPattern() {
        return filterPattern;
    }

    /**
     * @return True if filter is set to do exact matching, else false.
     */
    public boolean isExactMatch() {
        return exactMatch;
    }

    /**
     * Nicely format the filter string to be used for showing the sender.
     *
     * @return The formatted filter string.
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