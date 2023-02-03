package com.onarandombox.MultiverseCore.utils.webpaste;

import java.util.Map;

/**
 * An interface to a web-based text-pasting service. Classes extending this
 * should implement its methods to send data to an online text-sharing service,
 * such as pastebin.com. Given some PasteService instance ps, a paste is accomplished by:
 *
 * {@code ps.postData(someString);}
 *
 * Services that provide a distinction between "public" and "private" pastes
 * should implement a constructor that specifies which kind the PasteService
 * instance is submitting; an example of this is the PastebinPasteService class.
 */
public abstract class PasteService extends HttpAPIClient {
    PasteService(String url) {
        super(url);
    }

    PasteService(String url, String accessToken) {
        super(url, accessToken);
    }

    /**
     * Post data to the Web.
     *
     * @param data A String to post to the web.
     * @throws PasteFailedException When pasting/posting the data failed.
     * @return The URL at which the new paste is visible.
     */
    public abstract String postData(String data) throws PasteFailedException;

    /**
     * Post data to the Web.
     *
     * @param data A Map to post to the web.
     * @throws PasteFailedException When pasting/posting the data failed.
     * @return The URL at which the new paste is visible.
     */
    public abstract String postData(Map<String, String> data) throws PasteFailedException;

    /**
     * Does this service support uploading multiple files.
     *
     * Newer services like GitHub's Gist support multi-file pastes,
     * which allows us to upload configs in addition to the standard logs.
     *
     * @return True if this service supports multiple file upload.
     */
    public abstract boolean supportsMultiFile();
}
