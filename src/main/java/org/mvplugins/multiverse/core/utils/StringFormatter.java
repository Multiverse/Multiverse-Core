package org.mvplugins.multiverse.core.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    public static @NotNull String joinAnd(@Nullable List<String> list) {
        return join(list, ", ", " and ");
    }

    public static @NotNull String join(@Nullable Collection<?> list, @NotNull String separator) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return list.stream().map(String::valueOf).collect(Collectors.joining(separator));
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
    public static @NotNull String join(@Nullable List<String> list, @NotNull String separator, @NotNull String lastSeparator) {
        if (list == null || list.isEmpty()) {
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
     *
     * @param input     The current input
     * @param addons    The autocomplete suggestions
     * @return A collection of suggestions with the next suggestion appended
     *
     * @deprecated Method name has a spelling error. Use {@link #addOnToCommaSeparated(String, Collection)} instead.
     */
    @Deprecated(forRemoval = true, since = "5.5")
    @ApiStatus.ScheduledForRemoval(inVersion = "6.0")
    public static Collection<String> addonToCommaSeperated(@Nullable String input, @NotNull Collection<String> addons) {
        return addOnToCommaSeparated(input, addons);
    }

    /**
     * Appends a list of suggestions to the end of the input string, separated by commas.
     *
     * @param input     The current input
     * @param addons    The autocomplete suggestions
     * @return A collection of suggestions with the next suggestion appended
     *
     * @since 5.5
     */
    @ApiStatus.AvailableSince("5.5")
    public static Collection<String> addOnToCommaSeparated(@Nullable String input, @NotNull Collection<String> addons) {
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
    public static @NotNull Collection<String> parseQuotesInArgs(@NotNull String[] args) {
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
    @Contract("null -> null")
    public static @Nullable String quoteMultiWordString(@Nullable String input) {
        return input != null && input.contains(" ") ? "\"" + input + "\"" : input;
    }

    /**
     * Parses a CSV string of key=value pairs into a map.
     * E.g. "key1=value1,key2=value2" -> {key1=value1, key2=value2}
     *
     * @param input The CSV string to parse
     * @return The parsed map
     *
     * @since 5.5
     */
    @ApiStatus.AvailableSince("5.5")
    public static @Unmodifiable Map<String, String> parseCSVMap(@Nullable String input) {
        if (Strings.isNullOrEmpty(input)) {
            return Map.of();
        }
        return REPatterns.COMMA.splitAsStream(input)
                .map(s -> REPatterns.EQUALS.split(s, 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toUnmodifiableMap(parts -> parts[0], parts -> parts[1]));
    }
}
