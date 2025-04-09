package org.mvplugins.multiverse.core.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Common string formatting methods used throughout Multiverse plugins.
 */
public final class StringFormatter {

    private StringFormatter() {
        // no instance
    }

    /**
     * Concatenates a list of strings into a single string, using a comma and a space as separators,
     * and " and " as the separator before the last element.
     * <br/>
     * This method is useful for creating human-readable lists, such as "John, Mary and David".
     *
     * @param list the list of strings to join. If the list is empty, an empty string is returned.
     * @return the concatenated string
     */
    public static @NotNull String joinAnd(List<String> list) {
        return join(list, ", ", " and ");
    }

    public static @NotNull String join(Collection list, String separator) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(separator)).toString();
    }

    /**
     * Concatenates a list of strings into a single string, using a specified separator and a different separator
     * for the last element.
     *
     * @param list the list of strings to join. If the list is empty, an empty string is returned.
     * @param separator the separator to use between elements, except for the last one. For example, a comma and a space.
     * @param lastSeparator the separator to use before the last element. For example, " and ".
     * @return the concatenated string
     */
    public static @NotNull String join(List<String> list, String separator, String lastSeparator) {
        if (list.isEmpty()) {
            return "";
        }

        StringBuilder authors = new StringBuilder();
        authors.append(list.get(0));

        for (int i = 1; i < list.size(); i++) {
            if (i == list.size() - 1) {
                authors.append(lastSeparator).append(list.get(i));
            } else {
                authors.append(separator).append(list.get(i));
            }
        }

        return authors.toString();
    }

    /**
     * Appends a list of suggestions to the end of the input string, separated by commas.
     * @param input     The current input
     * @param addons    The autocomplete suggestions
     * @return A collection of suggestions with the next suggestion appended
     */
    public static Collection<String> addonToCommaSeperated(@Nullable String input, @NotNull Collection<String> addons) {
        if (Strings.isNullOrEmpty(input)) {
            return addons;
        }
        int lastComma = input.lastIndexOf(',');
        String previousInputs = input.substring(0, lastComma + 1);
        Set<String> inputSet = Sets.newHashSet(REPatterns.COMMA.split(input));
        return addons.stream()
                .filter(suggestion -> !inputSet.contains(suggestion))
                .map(suggestion -> previousInputs + suggestion)
                .toList();
    }

    /**
     * Parse quotes in args into a single string. E.g. ["\"my", "string\""] -> ["my string"]
     *
     * @param args  The args to parse
     * @return The parsed args
     */
    public static Collection<String> parseQuotesInArgs(String[] args) {
        List<String> result = new ArrayList<>(args.length);
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        int quoteStartIndex = -1;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (!inQuotes && arg.startsWith("\"") && !arg.endsWith("\"")) {
                inQuotes = true;
                quoteStartIndex = i;
                current.append(arg.substring(1));
            } else if (inQuotes && arg.endsWith("\"")) {
                current.append(" ").append(arg, 0, arg.length() - 1);
                result.add(current.toString());
                current.setLength(0);
                inQuotes = false;
                quoteStartIndex = -1;
            } else if (inQuotes) {
                current.append(" ").append(arg);
            } else if (arg.startsWith("\"") && arg.endsWith("\"") && arg.length() > 1) {
                // Fully quoted in one token
                result.add(arg.substring(1, arg.length() - 1));
            } else {
                result.add(arg);
            }
        }

        // If we never saw the end quote, treat all those args as individual tokens again
        if (inQuotes) {
            // Restore the original args from quoteStartIndex onward
            result.addAll(Arrays.asList(args).subList(quoteStartIndex, args.length));
        }

        return result;
    }

    /**
     * Add quotes to a string if it contains spaces. E.g. "my string" -> "\"my string\"".
     * Gives back the original string if it doesn't contain spaces.
     *
     * @param input The string to add quotes to
     * @return The quoted string
     */
    public static String quoteMultiWordString(String input) {
        return input.contains(" ") ? "\"" + input + "\"" : input;
    }
}
