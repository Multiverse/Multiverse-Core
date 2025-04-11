package org.mvplugins.multiverse.core.display.filters;

/**
 * Default implementation of {@link ContentFilter} that doesn't filter anything.
 */
public final class DefaultContentFilter implements ContentFilter {

    private static DefaultContentFilter instance;

    /**
     * Gets the singleton instance of this class.
     *
     * @return The singleton instance of this class.
     */
    public static DefaultContentFilter get() {
        if (instance == null) {
            instance = new DefaultContentFilter();
        }
        return instance;
    }

    private DefaultContentFilter() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkMatch(String value) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean needToFilter() {
        return false;
    }

    @Override
    public String toString() {
        return "N/A";
    }
}
