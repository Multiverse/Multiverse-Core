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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pastes to {@code pastie.org}.
 */
public class PastiePasteService implements PasteService {

    private boolean isPrivate;

    public PastiePasteService(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getPostURL() {
        try {
            return new URL("http://pastie.org/pastes");
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
            String encData = URLEncoder.encode("paste[authorization]", "UTF-8") + "=" + URLEncoder.encode("burger", "UTF-8"); // burger is magic
            encData += "&" + URLEncoder.encode("paste[restricted]", "UTF-8") + "=" + URLEncoder.encode(this.isPrivate ? "1" : "0", "UTF-8");
            encData += "&" + URLEncoder.encode("paste[parser_id]", "UTF-8") + "=" + URLEncoder.encode("6", "UTF-8"); // 6 is plain text
            encData += "&" + URLEncoder.encode("paste[body]", "UTF-8") + "=" + URLEncoder.encode(data, "UTF-8");
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
            String pastieUrl = "";
            Pattern pastiePattern = this.getURLMatchingPattern();
            while ((line = rd.readLine()) != null) {
                Matcher m = pastiePattern.matcher(line);
                if (m.matches()) {
                    String pastieID = m.group(1);
                    pastieUrl = this.formatURL(pastieID);
                }
            }
            return pastieUrl;
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

    private Pattern getURLMatchingPattern() {
        if (this.isPrivate) {
            return Pattern.compile(".*http://pastie.org/.*key=([0-9a-z]+).*");
        } else {
            return Pattern.compile(".*http://pastie.org/([0-9]+).*");
        }
    }

    private String formatURL(String pastieID) {
        return "http://pastie.org/" + (this.isPrivate ? "private/" : "") + pastieID;
    }
}
