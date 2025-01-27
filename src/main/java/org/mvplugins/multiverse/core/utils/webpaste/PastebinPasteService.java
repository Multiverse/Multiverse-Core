package org.mvplugins.multiverse.core.utils.webpaste;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Pastes to {@code pastebin.com}.
 */
final class PastebinPasteService extends PasteService {
    private final boolean isPrivate;
    private static final String PASTEBIN_POST_REQUEST = "https://pastebin.com/api/api_post.php";

    PastebinPasteService(boolean isPrivate) {
        super(PASTEBIN_POST_REQUEST);
        this.isPrivate = isPrivate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String encodeData(String data) {
        return URLEncoder.encode("api_dev_key", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("144d820f540e79a1242b32cb9ab274c6", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("api_option", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("paste", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("api_paste_code", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(data, StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("api_paste_private", StandardCharsets.UTF_8) + "=" + URLEncoder.encode(this.isPrivate ? "1" : "0", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("api_paste_format", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("yaml", StandardCharsets.UTF_8) +
                "&" + URLEncoder.encode("api_paste_name", StandardCharsets.UTF_8) + "=" + URLEncoder.encode("Multiverse-Core Debug Info", StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String encodeData(Map<String, String> data) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postData(String data) throws PasteFailedException {
        try {
            return this.exec(encodeData(data), ContentType.URLENCODED);
        } catch (IOException e) {
            throw new PasteFailedException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postData(Map<String, String> data) throws PasteFailedException {
        try {
            return this.exec(encodeData(data), ContentType.URLENCODED);
        } catch (IOException e) {
            throw new PasteFailedException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsMultiFile() {
        return false;
    }
}
