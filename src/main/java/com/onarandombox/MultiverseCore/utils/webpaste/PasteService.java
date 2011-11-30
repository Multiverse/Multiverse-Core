package com.onarandombox.MultiverseCore.utils.webpaste;

import java.net.URL;

public interface PasteService {
    /**
     * Encode the given String data into a format suitable for transmission in an HTTP request.
     *
     * @param data The raw data to encode.
     * @param isPrivate Whether the paste is desired to be private.
     * @return A URL-encoded string.
     */
    public String encodeData(String data);

    /**
     * Get the URL to which this paste service sends new pastes.
     *
     * @param isPrivate Whether the paste is desired to be private.
     * @return The URL that will be accessed to complete the paste.
     */
    public URL getPostURL();

    /**
     * Post encoded data to the Web.
     *
     * @param encodedData A URL-encoded String containing the full request to post to
     *                    the given URL. Can be the result of calling #encodeData().
     * @param url The URL to which to paste. Can be the result of calling #getPostURL().
     * @param isPrivate Whether the paste is desired to be private.
     * @return The URL at which the new paste is visible.
     */
    public String postData(String encodedData, URL url) throws PasteFailedException;
}
