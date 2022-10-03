package com.alexeykovzel.fi.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public abstract class EdgarService {

    // Links to EDGAR resources
    protected static final String SEC_URL = "https://www.sec.gov";
    protected static final String DATA_SEC_URL = "https://data.sec.gov";
    protected static final String FORM4_URL = SEC_URL + "/Archives/edgar/data/%s/%s.txt";
    protected static final String STOCKS_URL = SEC_URL + "/files/company_tickers_exchange.json";
    protected static final String FULL_INDEX_URL = SEC_URL + "/Archives/edgar/full-index/%d/QTR%d/master.idx";
    protected static final String SUBMISSIONS_URL = DATA_SEC_URL + "/submissions/%s.json";
    protected static final String FORM4_DAYS_AGO_URL = SEC_URL + "/cgi-bin/current?q1=%d&q3=4";
    protected static final String FORM4_RECENT_URL = SEC_URL + "/cgi-bin/browse-edgar" +
            "?action=getcurrent&type=4&company=&dateb=&owner=only&start=%d&count=%d&output=atom";

    // EDGAR API Configuration
    private static final String USER_AGENT_EMAIL = "alexey.kovzel@gmail.com";
    private static final String USER_AGENT_NAME = "FollowInsider";
    private static final String CONTENT_ENCODING = "gzip";
    private static final String CONTENT_CHARSET = "UTF-8";
    private static final int MAX_REQUEST_RETRIES = 3;
    private static final int REQUEST_TIMEOUT = 5000;
    private static final int REQUEST_DELAY = 120;

    private long lastRequestTime = 0;

    protected JsonNode getJsonByUrl(String url) throws IOException {
        try (InputStream in = getInputStreamByUrl(url, "application/json")) {
            if (in == null) throw new IOException("No Data");
            return new ObjectMapper().readTree(in);
        }
    }

    protected String getTextByUrl(String url) throws IOException {
        try (InputStream in = getInputStreamByUrl(url, "text/html")) {
            if (in == null) throw new IOException("No Data");
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    protected InputStream getInputStreamByUrl(String url, String contentType) {
        return getInputStreamByUrl(url, contentType, MAX_REQUEST_RETRIES);
    }

    private InputStream getInputStreamByUrl(String url, String contentType, int attempts) {
        if (attempts == 0) return null;
        long currentTime = System.currentTimeMillis();
        try {
            long delay = REQUEST_DELAY - (currentTime - lastRequestTime);
            TimeUnit.MILLISECONDS.sleep(delay);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = buildHttpRequest(url, contentType);
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            int statusCode = response.statusCode();
            // handle response status code
            switch (statusCode) {
                case 200:
                    // decode response body as input stream
                    String encoding = response.headers().firstValue("Content-Encoding").orElse("");
                    return decodeInputStream(response.body(), encoding);
                case 301:
                    // handle redirection to provided URL
                    Optional<String> redirectUrl = response.headers().firstValue("location");
                    return redirectUrl.map(s -> getInputStreamByUrl(s, contentType, attempts)).orElse(null);
                case 429:
                    // handle "too many requests" error
                    System.out.println("[ERROR] EDGAR: Too Many Requests");
                    return null;
                default:
                    System.out.println("[WARN] Unusual EDGAR response: " + statusCode);
                    return null;
            }
        } catch (IOException | InterruptedException e) {
            System.out.printf("[ERROR] %s: %d attempts left\n", e.getMessage(), attempts);
            return getInputStreamByUrl(url, contentType, attempts - 1);
        } finally {
            lastRequestTime = currentTime;
        }
    }

    private HttpRequest buildHttpRequest(String url, String contentType) {
        return HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.of(REQUEST_TIMEOUT, ChronoUnit.MILLIS))
                .header("User-Agent", USER_AGENT_NAME + " " + USER_AGENT_EMAIL)
                .header("Content-Type", contentType + ";charset=" + CONTENT_CHARSET)
                .header("Accept-Encoding", CONTENT_ENCODING)
                .build();
    }

    private InputStream decodeInputStream(InputStream inputStream, String encoding) throws IOException {
        switch (encoding) {
            case "":
                return inputStream;
            case "gzip":
                return new GZIPInputStream(inputStream);
            default:
                throw new UnsupportedOperationException("Unexpected Content-Encoding: " + encoding);
        }
    }
}
