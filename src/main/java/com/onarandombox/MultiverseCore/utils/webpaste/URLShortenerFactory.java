package com.onarandombox.MultiverseCore.utils.webpaste;

/**
 * Used to construct {@link URLShortener}s.
 */
public class URLShortenerFactory {
    private URLShortenerFactory() { }

    /**
     * Constructs a new {@link URLShortener}.
     * @param type The {@link URLShortenerType}.
     * @return The newly created {@link URLShortener}.
     */
    public static URLShortener getService(URLShortenerType type) {
        if (type == URLShortenerType.BITLY) {
            try {
                return new BitlyURLShortener();
            } catch (UnsupportedOperationException ignored) {}
        }

        return null;
    }
}