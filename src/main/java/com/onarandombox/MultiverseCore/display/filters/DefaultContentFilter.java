package com.onarandombox.MultiverseCore.display.filters;

/**
 * Default implementation of {@link ContentFilter} that doesn't filter anything.
 */
public class DefaultContentFilter implements ContentFilter {

    public static DefaultContentFilter instance;

    public static DefaultContentFilter getInstance() {
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
