package com.onarandombox.MultiverseCore.commandTools.display;

import com.dumptruckman.minecraft.util.Logging;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

    private void parseFilter() {
        if (filterString == null) {
            return;
        }
        if (filterString.startsWith("r=")) {
            Logging.info("custom");
            parseCustomFilter();
            return;
        }
        String cleanedFilter = REGEX_SPECIAL_CHARS.matcher(filterString.toLowerCase()).replaceAll("\\\\$0");
        this.filterPattern = Pattern.compile("(?i).*" + cleanedFilter + ".*");
    }

    private void parseCustomFilter() {
        try {
            this.filterPattern = Pattern.compile(filterString.substring(2));
            Logging.finest("Custom regex pattern: %s", this.filterPattern.toString());
        }
        catch (PatternSyntaxException ignored) {
            Logging.warning("Error parsing regex: %s", filterString);
        }
    }

    public boolean checkMatch(@Nullable String text) {
        if (text == null) {
            return false;
        }
        if (!hasFilter()) {
            return true;
        }
        if (!hasValidPattern()) {
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
