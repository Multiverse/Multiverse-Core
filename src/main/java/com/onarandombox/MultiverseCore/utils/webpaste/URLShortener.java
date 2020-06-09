package com.onarandombox.MultiverseCore.utils.webpaste;

/**
 * URL-Shortener.
 */
public abstract class URLShortener extends HttpAPIClient {
    public URLShortener(String url, String accessToken) {
        super(url, accessToken);
    }

    /**
     * Shorten a URL.
     * @param longUrl The long form.
     * @return The shortened URL.
     */
    public abstract String shorten(String longUrl);
}
