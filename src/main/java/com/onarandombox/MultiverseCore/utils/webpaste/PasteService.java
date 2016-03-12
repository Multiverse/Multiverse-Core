package com.onarandombox.MultiverseCore.utils.webpaste;

import java.net.URL;
import java.util.Map;

/**
 * An interface to a web-based text-pasting service. Classes implementing this
 * interface should implement its methods to send data to an online text-sharing
 * service, such as pastebin.com. Conventionally, a paste is accomplished by (given
 * some PasteService instance ps):
 *
 * {@code ps.postData(ps.encodeData(someString), ps.getPostURL());}
 *
 * Services that provide a distinction between "public" and "private" pastes
 * should implement a custom constructor that specifies which kind the PasteService
 * instance is submitting; an example of this is the PastiePasteService class.
 */
public interface PasteService {

    /**
     * Encode the given String data into a format suitable for transmission in an HTTP request.
     *
     * @param data The raw data to encode.
     * @return A URL-encoded string.
     */
    String encodeData(String data);

    /**
     * Encode the given Map data into a format suitable for transmission in an HTTP request.
     *
     * @param data The raw data to encode.
     * @return A URL-encoded string.
     */
    String encodeData(Map<String, String> data);

    /**
     * Get the URL to which this paste service sends new pastes.
     *
     * @return The URL that will be accessed to complete the paste.
     */
    URL getPostURL();

    /**
     * Post encoded data to the Web.
     *
     * @param encodedData A URL-encoded String containing the full request to post to
     *                    the given URL. Can be the result of calling #encodeData().
     * @param url The URL to which to paste. Can be the result of calling #getPostURL().
     * @throws PasteFailedException When pasting/posting the data failed.
     * @return The URL at which the new paste is visible.
     */
    String postData(String encodedData, URL url) throws PasteFailedException;

    /**
     * Does this service support uploading multiple files.
     *
     * Newer services like gist support multi-file which allows us to upload configs
     * in addition to the standard logs.
     *
     * @return True if this service supports multiple file upload.
     */
    boolean supportsMultiFile();
}
