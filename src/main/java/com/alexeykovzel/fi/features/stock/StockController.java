package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.trade.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockRatingRepository stockRatingRepository;
    private final StockRepository stockRepository;
    private final TradeRepository tradeRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/{symbol}")
    public StockView getStock(@PathVariable String symbol) {
        return getStockView(symbol);
    }

    public StockView getStockView(String symbol) {
        StockRating rating = stockRatingRepository.findBySymbol(symbol);
        Stock stock = stockRepository.findBySymbol(symbol);
        // verify that such stock and its rating exist
        if (rating == null || stock == null) {
            throw new IllegalArgumentException("Invalid stock symbol");
        }
        // then map its attributes to the view object
        String[] keyPoints = new StockKeyPointFactory().getKeyPoints(stock);
        Date lastActive = tradeRepository.findMaxDateByCik(stock.getCik());
        StockView view = modelMapper.map(stock, StockView.class);
        view.setKeyPoints(keyPoints);
        view.setLastActive(lastActive);
        view.setTrend(rating.getTrend());
        view.setEfficiency(rating.getEfficiency());
        view.setOverall(rating.getOverall());
        return view;
    }
}
