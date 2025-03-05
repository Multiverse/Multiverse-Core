package org.mvplugins.multiverse.core.utils.webpaste;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

final class McloGsPasteService extends PasteService {

    private static final String MCLOGS_POST_REQUEST = "https://api.mclo.gs/1/log";

    McloGsPasteService() {
        super(MCLOGS_POST_REQUEST);
    }

    @Override
    String encodeData(String data) {
        return "content=" + URLEncoder.encode(data, StandardCharsets.UTF_8);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String encodeData(Map<String, String> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String postData(String data) throws PasteFailedException {
        try {
            String stringJSON = this.exec(encodeData(data), ContentType.URLENCODED); // Execute request
            return String.valueOf(((JSONObject) new JSONParser().parse(stringJSON)).get("url")); // Interpret result
        } catch (IOException | ParseException e) {
            throw new PasteFailedException(e);
        }
    }

    @Override
    public String postData(Map<String, String> data) throws PasteFailedException {
        try {
            String stringJSON = this.exec(encodeData(data), ContentType.JSON); // Execute request
            return String.valueOf(((JSONObject) new JSONParser().parse(stringJSON)).get("url")); // Interpret result
        } catch (IOException | ParseException e) {
            throw new PasteFailedException(e);
        }
    }

    @Override
    public boolean supportsMultiFile() {
        return false;
    }
}
