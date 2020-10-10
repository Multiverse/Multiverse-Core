package com.onarandombox.MultiverseCore.utils.webpaste;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * HTTP API-client.
 */
abstract class HttpAPIClient {
    /**
     * The URL for this API-request, and if necessary, the access token.
     * If an access token is not necessary, it should be set to null.
     */
    private final String url;
    private final String accessToken;

    /**
     * Types of data that can be sent.
     */
    enum ContentType {
        JSON,
        PLAINTEXT,
        URLENCODED
    }

    HttpAPIClient(String url) {
        this(url, null);
    }

    HttpAPIClient(String url, String accessToken) {
        this.url = url;
        this.accessToken = accessToken;
    }

    /**
     * Returns the HTTP Content-Type header that corresponds with each ContentType.
     * @param type The type of data.
     * @return The HTTP Content-Type header that corresponds with the type of data.
     */
    private String getContentHeader(ContentType type) {
        switch (type) {
            case JSON:
                return "application/json; charset=utf-8";
            case PLAINTEXT:
                return "text/plain; charset=utf-8";
            case URLENCODED:
                return "application/x-www-form-urlencoded; charset=utf-8";
            default:
                throw new IllegalArgumentException("Unexpected value: " + type);
        }
    }

    /**
     * Encode the given String data into a format suitable for transmission in an HTTP request.
     *
     * @param data The raw data to encode.
     * @return A URL-encoded string.
     */
    abstract String encodeData(String data);

    /**
     * Encode the given Map data into a format suitable for transmission in an HTTP request.
     *
     * @param data The raw data to encode.
     * @return A URL-encoded string.
     */
    abstract String encodeData(Map<String, String> data);

    /**
     * Executes this API-Request.
     * @param payload The data that will be sent.
     * @param type The type of data that will be sent.
     * @return The result (as text).
     * @throws IOException When the I/O-operation failed.
     */
    final String exec(String payload, ContentType type) throws IOException {
        BufferedReader rd = null;
        OutputStreamWriter wr = null;

        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(this.url).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // we can receive anything!
            conn.addRequestProperty("Accept", "*/*");
            // set a dummy User-Agent
            conn.addRequestProperty("User-Agent", "placeholder");
            // this isn't required, but is technically correct
            conn.addRequestProperty("Content-Type", getContentHeader(type));
            // only some API requests require an access token
            if (this.accessToken != null) {
                conn.addRequestProperty("Authorization", this.accessToken);
            }

            wr = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8.newEncoder());
            wr.write(payload);
            wr.flush();

            String line;
            StringBuilder responseString = new StringBuilder();
            // this has to be initialized AFTER the data has been flushed!
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));

            while ((line = rd.readLine()) != null) {
                responseString.append(line);
            }

            return responseString.toString();
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
}
