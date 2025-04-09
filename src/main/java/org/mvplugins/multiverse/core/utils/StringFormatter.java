package org.mvplugins.multiverse.core.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
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

    public static Collection<String> parseQuotesInArgs(String[] args) {
        List<String> result = new ArrayList<>(args.length);
        StringBuilder quotedArg = null;
        for (String arg : args) {
            if (quotedArg == null && arg.startsWith("\"")) {
                // Handle edge case where the arg itself starts and ends with quotes without space: "arghere"
                if (arg.endsWith("\"")) {
                    result.add(arg.substring(1, arg.length() - 1));
                    continue;
                }
                quotedArg = new StringBuilder(arg.substring(1));
            } else if (quotedArg != null) {
                if (arg.endsWith("\"")) {
                    quotedArg.append(" ").append(arg, 0, arg.length() - 1);
                    result.add(quotedArg.toString());
                    quotedArg = null;
                    continue;
                }
                quotedArg.append(" ").append(arg);
            } else {
                result.add(arg);
            }
        }
        return result;
    }
}
