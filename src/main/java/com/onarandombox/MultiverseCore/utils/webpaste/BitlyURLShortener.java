package com.onarandombox.MultiverseCore.utils.webpaste;

import java.io.IOException;

/**
 * An {@link URLShortener} using {@code bit.ly}.
 */
public class BitlyURLShortener extends HttpAPIClient implements URLShortener {
    private static final String GENERIC_BITLY_REQUEST_FORMAT = "https://api-ssl.bitly.com/v3/shorten?format=txt&apiKey=%s&login=%s&longUrl=%s";

    // I think it's no problem that these are public
    private static final String USERNAME = "multiverse2";
    private static final String API_KEY = "R_9dbff4862a3bc0c4218a7d78cc10d0e0";

    public BitlyURLShortener() {
        super(String.format(GENERIC_BITLY_REQUEST_FORMAT, API_KEY, USERNAME, "%s"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String shorten(String longUrl) {
        try {
            String result = this.exec(longUrl);
            if (!result.startsWith("http://j.mp/")) // ... then it's failed :/
                throw new IOException(result);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return longUrl; // sorry ...
        }
    }
}
