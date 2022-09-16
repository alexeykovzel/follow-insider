package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.company.Company;
import com.alexeykovzel.fi.utils.DateUtils;
import com.alexeykovzel.fi.utils.YamlPropertyFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

@Service
@PropertySource(value = "classpath:rapid-api.yml", factory = YamlPropertyFactory.class)
public class AlphaVantageAPI {
    private static final String HOST_URL = "https://alpha-vantage.p.rapidapi.com";
    private static final String FUNCTION_URL = HOST_URL + "/query?function=%s&symbol=%s&datatype=%s";

    @Value("${rapid.api.key}")
    private String rapidApiKey;

    @Value("${rapid.api.host}")
    private String rapidApiHost;

    public Collection<StockRecord> getStockRecords(Company company) {
        Collection<StockRecord> records = new HashSet<>();

        // retrieve time series for a given symbol
        JsonNode timeSeries = getTimeSeriesBySymbol(company.getSymbol());
        if (timeSeries == null) return records;

        // save stock price and dividends for each date
        Iterator<String> dates = timeSeries.fieldNames();
        while (dates.hasNext()) {
            String date = dates.next();
            JsonNode stats = timeSeries.get(date);
            records.add(StockRecord.builder()
                    .date(DateUtils.parseEdgar(date))
                    .price(stats.get("5. adjusted close").asDouble())
                    .dividends(stats.get("7. dividend amount").asDouble())
                    .company(company)
                    .build());
        }
        return records;
    }

    private JsonNode getTimeSeriesBySymbol(String symbol) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(FUNCTION_URL, "TIME_SERIES_WEEKLY_ADJUSTED", symbol, "json")))
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", rapidApiHost)
                .build();
        try {
            InputStream in = client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body();
            return new ObjectMapper().readTree(in).get("Weekly Adjusted Time Series");
        } catch (IOException | InterruptedException e) {
            System.out.println("[ERROR] Could not access stock price: " + e.getMessage());
            return null;
        }
    }
}
