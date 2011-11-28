package com.onarandombox.MultiverseCore.utils.webpaste;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PastebinPasteService implements PasteService {

    protected boolean isPrivate;

    public PastebinPasteService(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public URL getPostURL() {
        try {
            return new URL("http://pastebin.com/api/api_post.php");
        } catch (MalformedURLException e) {
            return null; // should never hit here
        }
    }

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

    public String postData(String encodedData, URL url) throws PasteFailedException {
        try {
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(encodedData);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String pastebinUrl = "";
            while ((line = rd.readLine()) != null) {
                pastebinUrl = line;
            }
            wr.close();
            rd.close();
            return pastebinUrl;
        } catch (Exception e) {
            throw new PasteFailedException(e);
        }
    }
    
    private Pattern getURLMatchingPattern() {
        if(this.isPrivate) {
            return Pattern.compile(".*http://pastie.org/.*key=([0-9a-z]+).*");
        } else {
            return Pattern.compile(".*http://pastie.org/([0-9]+).*");
        }
    }

    private String formatURL(String pastieID) {
        return "http://pastie.org/" + (this.isPrivate ? "private/" : "") + pastieID;
    }
}
