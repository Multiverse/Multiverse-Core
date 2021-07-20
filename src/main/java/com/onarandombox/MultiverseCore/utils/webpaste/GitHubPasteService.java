package com.onarandombox.MultiverseCore.utils.webpaste;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Pastes to {@code gist.github.com}. Requires an access token with the {@code gist} scope.
 */
class GitHubPasteService extends PasteService {
    private final boolean isPrivate;
    // this access token must have the "gist" scope
    private static final String ACCESS_TOKEN = "token github-access-token";
    private static final String GITHUB_POST_REQUEST = "https://api.github.com/gists";

    GitHubPasteService(boolean isPrivate) {
        super(GITHUB_POST_REQUEST, ACCESS_TOKEN);
        this.isPrivate = isPrivate;
        if (ACCESS_TOKEN.endsWith("access-token")) {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String encodeData(String data) {
        Map<String, String> mapData = new HashMap<String, String>();
        mapData.put("multiverse.txt", data);
        return this.encodeData(mapData);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    String encodeData(Map<String, String> files) {
        JSONObject root = new JSONObject();
        root.put("description", "Multiverse-Core Debug Info");
        root.put("public", !this.isPrivate);
        JSONObject fileList = new JSONObject();
        for (Map.Entry<String, String> entry : files.entrySet()) {
            JSONObject fileObject = new JSONObject();
            fileObject.put("content", entry.getValue());
            fileList.put(entry.getKey(), fileObject);
        }

        root.put("files", fileList);
        return root.toJSONString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postData(String data) throws PasteFailedException {
        try {
            String stringJSON = this.exec(encodeData(data), ContentType.JSON);
            return (String) ((JSONObject) new JSONParser().parse(stringJSON)).get("html_url");
        } catch (IOException | ParseException e) {
            throw new PasteFailedException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postData(Map<String, String> data) throws PasteFailedException {
        try {
            String stringJSON = this.exec(encodeData(data), ContentType.JSON);
            return (String) ((JSONObject) new JSONParser().parse(stringJSON)).get("html_url");
        } catch (IOException | ParseException e) {
            throw new PasteFailedException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsMultiFile() {
        return true;
    }
}
