package org.mvplugins.multiverse.core.utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
}
