package com.onarandombox.MultiverseCore.utils.webpaste;

/**
 * An interface to a web-based URL Shortener. Classes extending this should
 * implement its methods to shorten links using the service. Given some
 * URLShortener instance us, a URL is shortened by:
 *
 * {@code us.shorten(longUrl);}
 *
 * An example of this, is the BitlyURLShortener.
 */
public abstract class URLShortener extends HttpAPIClient {
    URLShortener(String url) {
        super(url);
    }

    URLShortener(String url, String accessToken) {
        super(url, accessToken);
    }

    /**
     * Shorten a URL.
     * @param longUrl The long form.
     * @return The shortened URL.
     */
    public abstract String shorten(String longUrl);
}
