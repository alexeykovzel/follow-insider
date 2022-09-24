package com.alexeykovzel.fi.core.stock;

import com.alexeykovzel.fi.core.EdgarService;
import com.alexeykovzel.fi.api.AlphaVantageAPI;
import com.alexeykovzel.fi.core.insider.InsiderRepository;
import com.alexeykovzel.fi.core.trade.TradeRepository;
import com.alexeykovzel.fi.utils.ProgressBar;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    private final StockRatingStrategy ratingStrategy;
    private final InsiderRepository insiderRepository;
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
        InputStream resource = loader.getResourceAsStream("data/company-tickers.json");
        try {
            return getRootStocks(new ObjectMapper().readTree(resource));
        } catch (IOException e) {
            System.out.println("[ERROR] Invalid stock data (local): " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Collection<Stock> getStocksRemotely() {
        try {
            return getRootStocks(getJsonByUrl(STOCKS_URL));
        } catch (IOException e) {
            System.out.println("[ERROR] Invalid stock data (remote): " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private Collection<Stock> getRootStocks(JsonNode root) {
        Collection<Stock> stocks = new HashSet<>();
        try {
            root.get("data").forEach(item -> stocks.add(Stock.builder()
                    .cik(addLeadingZeros(item.get(0).asText()))
                    .name(normalize(item.get(1).asText()))
                    .symbol(item.get(2).asText())
                    .exchange(item.get(3).asText())
                    .build())
            );
        } catch (NullPointerException e) {
            System.out.println("[ERROR] Failed to access stock data: " + e.getMessage());
        }
        return stocks;
    }

    public boolean existsBySymbol(String symbol) {
        return stockRepository.existsBySymbol(symbol.toUpperCase());
    }

    public Optional<StockView> getBySymbol(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase());
        return getStockView(stock);
    }

    public List<StockView> getAll() {
        return null;
    }

    public Optional<StockView> getStockView(Stock stock) {
        if (stock == null) return Optional.empty();
        String cik = stock.getCik();
        // automatically set name, symbol, description
        StockView view = new ModelMapper().map(stock, StockView.class);
        // set key points as brief sentences with main info about the stock
        view.setKeyPoints(new StockKeyPointFactory().getKeyPoints(stock));
        // set last active date as the date of the last transaction
        view.setLastActive(tradeRepository.findMaxDateByStock(cik));
        // set stock rating (if exists)
        stockRatingRepository.findById(cik).ifPresent(rating -> {
            view.setTrend(rating.getTrend());
            view.setEfficiency(rating.getEfficiency());
            view.setOverall(rating.getOverall());
        });
        // set insider and return stock view
        view.setInsiders(insiderRepository.findViewsByStock(cik));
        return Optional.of(view);
    }
}
