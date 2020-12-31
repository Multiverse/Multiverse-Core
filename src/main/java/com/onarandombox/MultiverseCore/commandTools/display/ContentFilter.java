package com.onarandombox.MultiverseCore.commandTools.display;

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
    private final String filterString;
    private Pattern filterPattern;
    private boolean exactMatch;

    private static final Pattern REGEX_SPECIAL_CHARS = Pattern.compile("[.+*?\\[^\\]$(){}=!<>|:-\\\\]");
    public static ContentFilter EMPTY = new ContentFilter();

    public ContentFilter() {
        this(null);
    }

    public ContentFilter(@Nullable String filterString) {
        this(filterString, false);
    }

    public ContentFilter(@Nullable String filterString,
                         boolean exactMatch) {

        this.filterString = filterString;
        this.exactMatch = exactMatch;
        parseFilter();
    }

    /**
     * Compile regex pattern based on {@link ContentFilter#filterString}.
     */
    private void parseFilter() {
        if (filterString == null) {
            return;
        }
        if (filterString.startsWith("r=")) {
            Logging.info("custom");
            parseCustomFilter();
            return;
        }
        parseContainsFilter();
    }

    /**
     * When prefixed with 'r=', use {@link ContentFilter#filterString} as the full regex pattern.
     */
    private void parseCustomFilter() {
        try {
            this.filterPattern = Pattern.compile(filterString.substring(2));
            Logging.finest("Custom regex pattern: %s", this.filterPattern.toString());
        }
        catch (PatternSyntaxException ignored) {
            Logging.warning("Error parsing regex: %s", filterString);
        }
    }

    /**
     * Set pattern that matches any text that contains {@link ContentFilter#filterString}.
     */
    private void parseContainsFilter() {
        String cleanedFilter = REGEX_SPECIAL_CHARS.matcher(filterString.toLowerCase()).replaceAll("\\\\$0");
        this.filterPattern = Pattern.compile("(?i).*" + cleanedFilter + ".*");
    }

    /**
     * Do regex matching.
     *
     * @param text String to check regex on.
     * @return True of matches regex pattern, false otherwise.
     */
    public boolean checkMatch(@Nullable String text) {
        if (!hasFilter()) {
            return true;
        }
        if (text == null || !hasValidPattern()) {
            return false;
        }
        text = ChatColor.stripColor(text);
        return (exactMatch)
                ? filterPattern.matcher(text).matches()
                : filterPattern.matcher(text).find();
    }

    public boolean hasFilter() {
        return filterString != null;
    }

    public boolean hasValidPattern() {
        return filterPattern != null;
    }

    @Nullable
    public String getString() {
        return filterString;
    }

    @Nullable
    public Pattern getPattern() {
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
    @NotNull
    public String getFormattedString() {
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
