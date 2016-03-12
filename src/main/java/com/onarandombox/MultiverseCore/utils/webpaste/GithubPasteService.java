package com.onarandombox.MultiverseCore.utils.webpaste;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class GithubPasteService implements PasteService {

    private final boolean isPrivate;

    public GithubPasteService(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @Override
    public String encodeData(String data) {
        Map<String, String> mapData = new HashMap<String, String>();
        mapData.put("multiverse.txt", data);
        return this.encodeData(mapData);
    }

    @Override
    public String encodeData(Map<String, String> files) {
        JSONObject root = new JSONObject();
        String result = "";
        try {
            root.put("description", "Multiverse-Core Debug Info");
            root.put("public", !this.isPrivate);
            JSONObject fileList = new JSONObject();
            for (Map.Entry<String, String> entry : files.entrySet())
            {
                JSONObject fileObject = new JSONObject();
                fileObject.put("content", entry.getValue());
                fileList.put(entry.getKey(), fileObject);
            }
            root.put("files", fileList);
            result = root.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public URL getPostURL() {
        try {
            return new URL("https://api.github.com/gists");
            //return new URL("http://jsonplaceholder.typicode.com/posts");
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
            wr.write(encodedData);
            wr.flush();

            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String pastieUrl = "";
            //Pattern pastiePattern = this.getURLMatchingPattern();
            StringBuilder responseString = new StringBuilder();

            while ((line = rd.readLine()) != null) {
                responseString.append(line);
            }
            JSONObject response = new JSONObject(responseString.toString());
            return response.get("html_url").toString();
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
        return true;
    }
}
