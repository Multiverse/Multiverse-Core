package com.onarandombox.MultiverseCore.utils.webpaste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Pastes to {@code pastebin.com}.
 */
public class PastebinPasteService implements PasteService {

    private boolean isPrivate;

    public PastebinPasteService(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getPostURL() {
        try {
            return new URL("http://pastebin.com/api/api_post.php");
        } catch (MalformedURLException e) {
            return null; // should never hit here
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String encodeData(String data) {
        try {
            String encData = URLEncoder.encode("api_dev_key", "UTF-8") + "=" + URLEncoder.encode("d61d68d31e8e0392b59b50b277411c71", "UTF-8");
            encData += "&" + URLEncoder.encode("api_option", "UTF-8") + "=" + URLEncoder.encode("paste", "UTF-8");
            encData += "&" + URLEncoder.encode("api_paste_code", "UTF-8") + "=" + URLEncoder.encode(data, "UTF-8");
            encData += "&" + URLEncoder.encode("api_paste_private", "UTF-8") + "=" + URLEncoder.encode(this.isPrivate ? "1" : "0", "UTF-8");
            encData += "&" + URLEncoder.encode("api_paste_format", "UTF-8") + "=" + URLEncoder.encode("yaml", "UTF-8");
            return encData;
        } catch (UnsupportedEncodingException e) {
            return ""; // should never hit here
        }
    }

    @Override
    public String encodeData(Map<String, String> data) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String postData(String encodedData, URL url) throws PasteFailedException {
        OutputStreamWriter wr = null;
        BufferedReader rd = null;
        try {
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(encodedData);
            wr.flush();

            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String pastebinUrl = "";
            while ((line = rd.readLine()) != null) {
                pastebinUrl = line;
            }
            return pastebinUrl;
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
