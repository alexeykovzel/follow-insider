package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.EdgarService;
import com.alexeykovzel.fi.features.stock.news.StockNewsFactory;
import com.alexeykovzel.fi.features.stock.rating.StockRating;
import com.alexeykovzel.fi.features.stock.rating.StockRatingRepository;
import com.alexeykovzel.fi.features.stock.rating.StockRatingStrategy;
import com.alexeykovzel.fi.features.stock.record.AlphaVantageAPI;
import com.alexeykovzel.fi.features.stock.record.StockRecordRepository;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.utils.ProgressBar;
import com.alexeykovzel.fi.utils.StringUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService extends EdgarService {
    private final StockRatingRepository stockRatingRepository;
    private final StockRecordRepository stockRecordRepository;
    private final StockRatingStrategy ratingStrategy;
    private final StockNewsFactory stockNewsFactory;
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
    private final AlphaVantageAPI alphaVantageAPI;

    public void updateStocksLocally() {
        updateStocks(getStocksLocally());
    }

    public void updateStocksRemotely() {
        updateStocks(getStocksRemotely());
    }

    @Transactional
    public void updateStocks(Collection<Stock> stocks) {
        ProgressBar.execute("Updating stocks...", () -> stockRepository.saveAll(stocks));
    }

    @Transactional
    public void updateStockRecords() {
        stockRecordRepository.deleteAll();
        ProgressBar.execute("Updating stock records...", stockRepository.findAll(), stock ->
                stockRecordRepository.saveAll(alphaVantageAPI.getStockRecords(stock))
        );
    }

    @Transactional
    public void updateStockRating() {
        Collection<StockRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating stock rating...", stockRepository.findAll(), stock ->
                ratings.add(StockRating.builder()
                        .efficiency(ratingStrategy.calculateEfficiency(stock))
                        .trend(ratingStrategy.calculateTrend(stock))
                        .stock(stock)
                        .build()));
        stockRatingRepository.saveAll(ratings);
    }

    private Collection<Stock> getStocksLocally() {
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream resource = loader.getResourceAsStream("edgar/company-tickers.json");
        try {
            return getRootStocks(new ObjectMapper().readTree(resource));
        } catch (IOException e) {
            log.error("Invalid stock data (local): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private Collection<Stock> getStocksRemotely() {
        try {
            return getRootStocks(getJsonByUrl(STOCKS_URL));
        } catch (IOException e) {
            log.error("Invalid stock data (remote): {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    private Collection<Stock> getRootStocks(JsonNode root) {
        Collection<Stock> stocks = new HashSet<>();
        try {
            root.get("data").forEach(item -> stocks.add(Stock.builder()
                    .cik(StringUtils.addLeadingZeros(item.get(0).asText()))
                    .name(formatStockName(item.get(1).asText()))
                    .symbol(item.get(2).asText())
                    .exchange(item.get(3).asText())
                    .build())
            );
        } catch (NullPointerException e) {
            log.error("Failed to access stock data: {}", e.getMessage());
        }
        return stocks;
    }

    public Optional<StockView> getStockView(Stock stock) {
        if (stock == null) return Optional.empty();
        String cik = stock.getCik();
        // automatically set name, symbol, description
        StockView view = new ModelMapper().map(stock, StockView.class);
        // set key points as brief sentences with main info about the stock
        view.setKeyPoints(stockNewsFactory.buildNews(stock, 3));
        // set last active date as the date of the last transaction
        view.setLastActive(tradeRepository.findMaxDateByCik(cik));
        // set stock rating (if exists)
        stockRatingRepository.findById(cik).ifPresent(rating -> {
            view.setTrend(rating.getTrend());
            view.setEfficiency(rating.getEfficiency());
            view.setOverall(rating.getOverall());
        });
        return Optional.of(view);
    }

    private String formatStockName(String val) {
        val = StringUtils.toCamelCase(val);
        val = StringUtils.addDots(val);
        return val;
    }
}
