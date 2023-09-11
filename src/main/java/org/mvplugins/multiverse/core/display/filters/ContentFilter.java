package org.mvplugins.multiverse.core.display.filters;

/**
 * Filter display content for it show only certain string.
 */
public interface ContentFilter {
    /**
     * Checks if a particular string should be displayed.
     *
     * @param value String to check on.
     * @return True if should be display, false otherwise.
     */
    boolean checkMatch(String value);

    /**
     * Gets whether content needs to be filtered by this filter.
     *
     * @return True if content should be filtered, false otherwise.
     */
    boolean needToFilter();

    /**
     * Gets the string representation of this filter.
     *
     * @return The string representation of this filter.
     */
    String toString();
}
