package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.EdgarService;
import com.alexeykovzel.fi.features.stock.api.AlphaVantageAPI;
import com.alexeykovzel.fi.features.trade.TradeRating;
import com.alexeykovzel.fi.features.trade.TradeRatingRepository;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.utils.DateUtils;
import com.alexeykovzel.fi.utils.ProgressBar;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StockService extends EdgarService {
    private final StockRatingRepository stockRatingRepository;
    private final StockRecordRepository stockRecordRepository;
    private final TradeRatingRepository tradeRatingRepository;
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
    private final AlphaVantageAPI alphaVantageAPI;

    // weights for different number of months after the transaction
    private static final Map<Integer, Double> TREND_WEIGHTS = Map.of(
            1, 0.6,
            2, 0.3,
            3, 0.1
    );

    @Transactional
    public void updateStockRecords() {
        stockRecordRepository.deleteAll();
        ProgressBar.execute("Updating stock records...", stockRepository.findAll(), stock ->
                stockRecordRepository.saveAll(alphaVantageAPI.getStockRecords(stock))
        );
    }

    /**
     * Updates data of all public stocks stored in the SEC collection.
     */
    @Transactional
    public void updateStocks() {
        ProgressBar.execute("Updating stocks...", () -> {
            try {
                stockRepository.saveAll(fetchStocks());
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to update stocks");
            }
        });
    }

    /**
     * Updates financial metrics of all public stocks stored in the system.
     */
    @Transactional
    public void updateStockRating() {
        Collection<StockRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating stock rating...", stockRepository.findAll(), stock ->
                ratings.add(StockRating.builder()
                        .efficiency(calculateEfficiency(stock))
                        .trend(calculateTrend(stock))
                        .stock(stock)
                        .build()));
        stockRatingRepository.saveAll(ratings);
    }

    /**
     * Calculates the efficiency of stock insiders as a number between 0 and 1, which indicates if an insider
     * performs better or worse than an average investor.
     *
     * @param stock stock which insiders are taken for evaluation
     * @return number between 0 and 1 that indicates the insiders' efficiency
     */
    public double calculateEfficiency(Stock stock) {
        double efficiency = 0;
        double totalWeight = 0;
        for (TradeRating rating : tradeRatingRepository.findByStockCik(stock.getCik())) {
            efficiency += rating.getEfficiency() * rating.getWeight();
            totalWeight += rating.getWeight();
        }
        return (totalWeight != 0) ? (efficiency / totalWeight) : 0;
    }

    /**
     * Calculates the trend (bearish/bullish) of stock insiders as a number between 0 and 1, which
     * indicates if the number of purchases changed comparing to historic values.
     *
     * @param stock stock which insiders are taken for evaluation
     * @return number between 0 and 1 that indicates the insiders' trend
     */
    public double calculateTrend(Stock stock) {
        String cik = stock.getCik();
        Date minDate = tradeRepository.findMinDateByCik(cik);
        if (minDate == null) return 0;
        Date currentDate = new Date();
        double months = DateUtils.monthsBetween(minDate, currentDate);
        double average = tradeRepository.findPurchaseCountByCik(cik) / months;
        double totalTrend = 0;

        for (int i = 1; i <= Math.min(months, TREND_WEIGHTS.size()); i++) {
            Date pastDate = DateUtils.shiftMonths(currentDate, -i);
            int count = tradeRepository.findPurchaseCountByCik(cik, pastDate, currentDate);
            double trend = Math.min(1, (count - average) / average);
            totalTrend += trend * TREND_WEIGHTS.get(i);
        }
        return totalTrend;
    }

    private Collection<Stock> getStocksLocally() throws IOException {
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream resource = loader.getResourceAsStream("edgar/company-tickers.json");
        return getStocks(new ObjectMapper().readTree(resource));
    }

    private Collection<Stock> fetchStocks() throws IOException {
        return getStocks(getJsonByUrl(STOCKS_URL));
    }

    private Collection<Stock> getStocks(JsonNode root) {
        Collection<Stock> stocks = new HashSet<>();
        try {
            root.get("data").forEach(item -> stocks.add(Stock.builder()
                    .cik(addLeadingZeros(item.get(0).asText()))
                    .name(normalize(item.get(1).asText()))
                    .symbol(item.get(2).asText())
                    .exchange(item.get(3).asText())
                    .build()));
        } catch (NullPointerException e) {
            System.out.println("[ERROR] Failed to access stock data: " + e.getMessage());
        }
        return stocks;
    }
}
