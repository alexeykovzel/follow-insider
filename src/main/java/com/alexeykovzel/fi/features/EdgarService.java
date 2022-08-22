package com.alexeykovzel.fi.features;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public abstract class EdgarService {
    protected static final String SEC_URL = "https://www.sec.gov";
    protected static final String COMPANIES_URL = SEC_URL + "/files/company_tickers_exchange.json";
    protected static final String FULL_INDEX_URL = SEC_URL + "/Archives/edgar/full-index/%d/QTR%d/master.idx";
    protected static final String FORM4_URL = SEC_URL + "/Archives/edgar/data/%s/%s.txt";
    protected static final String FORM4_RECENT_URL = SEC_URL + "/cgi-bin/browse-edgar?action=getcurrent&type=4&company=&dateb=&owner=only&start=%d&count=%d&output=atom";
    protected static final String FORM4_DAYS_AGO_URL = SEC_URL + "/cgi-bin/current?q1=%d&q3=4";
    protected static final String SUBMISSIONS_URL = SEC_URL + "/submissions/%s.json";

    private static final int MAX_REQUEST_RETRIES = 3;
    private static final int REQUEST_TIMEOUT = 5000;
    private static final int REQUEST_DELAY = 100;

    private long lastRequestTime = 0;

    protected String normalize(String val) {
        val = capitalize(val);
        val = addDots(val);
        return val;
    }

    protected String addLeadingZeros(String value) {
        return "0".repeat(10 - value.length()) + value;
    }

    protected JsonNode getJsonByUrl(String url) throws IOException {
        try (InputStream in = sendUrlRequest(url)) {
            return (in != null) ? new ObjectMapper().readTree(in) : null;
        }
    }

    protected String getTextByUrl(String url) throws IOException {
        try (InputStream in = sendUrlRequest(url)) {
            return (in != null) ? new String(in.readAllBytes(), StandardCharsets.UTF_8) : null;
        }
    }

    protected InputStream sendUrlRequest(String url) {
        return sendUrlRequest(url, MAX_REQUEST_RETRIES);
    }

    private InputStream sendUrlRequest(String url, int attempts) {
        if (attempts == 0) return null;
        long currentTime = System.currentTimeMillis();
        try {
            long delay = REQUEST_DELAY - (currentTime - lastRequestTime);
            TimeUnit.MILLISECONDS.sleep(delay);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.of(REQUEST_TIMEOUT, ChronoUnit.MILLIS))
                    .header("User-Agent", "Insidr alexey.kovzel@gmail.com")
                    .header("Accept-Encoding", "gzip")
                    .build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            String encoding = response.headers().firstValue("Content-Encoding").orElse("");
            return decodeInputStream(response.body(), encoding);
        } catch (IOException | InterruptedException e) {
            System.out.println("[ERROR] " + e.getMessage() + ": " + attempts + " attempts left");
            return sendUrlRequest(url, attempts - 1);
        } finally {
            lastRequestTime = currentTime;
        }
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

    private String capitalize(String val) {
        Set<Character> reservedChars = Set.of(' ', '.');
        char[] chars = val.toLowerCase().toCharArray();
        char[] capitalizedVal = new char[chars.length];
        char prevChar = ' ';
        for (int i = 0; i < chars.length; i++) {
            capitalizedVal[i] = reservedChars.contains(prevChar)
                    ? Character.toUpperCase(chars[i]) : chars[i];
            prevChar = chars[i];
        }
        return String.valueOf(capitalizedVal);
    }

    private String addDots(String val) {
        Set<String> dottedWords = Set.of("Corp", "Inc", "Ltd", "Co");
        String[] words = val.split(" ");
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (dottedWords.contains(word)) {
                words[i] = word + ".";
            }
        }
        return String.join(" ", words);
    }
}
