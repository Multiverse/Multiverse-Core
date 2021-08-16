package com.onarandombox.MultiverseCore.display.filters;

/**
 * Default implementation of {@link ContentFilter} that doesn't filter anything.
 */
public class DefaultContentFilter implements ContentFilter {

    public static DefaultContentFilter INSTANCE = new DefaultContentFilter();

    public DefaultContentFilter() {
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
}
