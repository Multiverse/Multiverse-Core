package com.onarandombox.MultiverseCore.utils.webpaste;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

/**
 * Pastes to {@code hastebin.com}.
 */
public class HastebinPasteService implements PasteService {

    @Override
    public String encodeData(String data) {
        return data;
    }

    @Override
    public String encodeData(Map<String, String> data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URL getPostURL() {
        try {
            return new URL("https://hastebin.com/documents");
        } catch (MalformedURLException e) {
            return null; // should never hit here
        }
    }

    @Override
    public String postData(String encodedData, URL url) throws PasteFailedException {
        OutputStreamWriter wr = null;
        BufferedReader rd = null;
        try {
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            wr = new OutputStreamWriter(conn.getOutputStream());
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            wr.write(encodedData);
            wr.flush();

            String line;
            StringBuilder responseString = new StringBuilder();
            while ((line = rd.readLine()) != null) {
                responseString.append(line);
            }
            String key = new JsonParser().parse(responseString.toString()).getAsJsonObject().get("key").getAsString();

            return "https://hastebin.com/" + key;
        } catch (Exception e) {
            throw new PasteFailedException(e);
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException ignore) { }
            }
            if (rd != null) {
                try {
                    rd.close();
                } catch (IOException ignore) { }
            }
        }
    }

    @Override
    public boolean supportsMultiFile() {
        return false;
    }
}
