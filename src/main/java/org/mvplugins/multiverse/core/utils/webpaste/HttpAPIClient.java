package org.mvplugins.multiverse.core.utils.webpaste;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * HTTP API-client.
 */
abstract sealed class HttpAPIClient permits PasteService, URLShortener {
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
        PLAINTEXT_YAML,
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
        return switch (type) {
            case JSON -> "application/json; charset=utf-8";
            case PLAINTEXT -> "text/plain; charset=utf-8";
            case PLAINTEXT_YAML -> "text/yaml";
            case URLENCODED -> "application/x-www-form-urlencoded; charset=utf-8";
            default -> throw new IllegalArgumentException("Unexpected value: " + type);
        };
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
        BufferedReader bufferedReader = null;
        OutputStreamWriter streamWriter = null;

        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(this.url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // we can receive anything!
            connection.addRequestProperty("Accept", "*/*");
            // set a dummy User-Agent
            connection.addRequestProperty("User-Agent", "multiverse/dumps");
            // this isn't required, but is technically correct
            connection.addRequestProperty("Content-Type", getContentHeader(type));
            // only some API requests require an access token
            if (this.accessToken != null) {
                connection.addRequestProperty("Authorization", this.accessToken);
            }

            streamWriter = new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8.newEncoder());
            streamWriter.write(payload);
            streamWriter.flush();

            String line;
            StringBuilder responseString = new StringBuilder();
            // this has to be initialized AFTER the data has been flushed!
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }

            return responseString.toString();
        } finally {
            if (streamWriter != null) {
                try {
                    streamWriter.close();
                } catch (IOException ignore) { }
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ignore) { }
            }
        }
    }
}
