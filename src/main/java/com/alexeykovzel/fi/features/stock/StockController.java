package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.trade.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {
    private final StockRatingRepository stockRatingRepository;
    private final StockRepository stockRepository;
    private final TradeRepository tradeRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/{symbol}")
    public StockView getStockBySymbol(@PathVariable String symbol) {
        return getStockView(stockRepository.findBySymbol(symbol))
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock symbol: " + symbol));
    }

    public Optional<StockView> getStockView(Stock stock) {
        if (stock == null) return Optional.empty();
        String cik = stock.getCik();
        StockView view = modelMapper.map(stock, StockView.class);
        view.setKeyPoints(new StockKeyPointFactory().getKeyPoints(stock));
        view.setLastActive(tradeRepository.findMaxDateByCik(cik));
        stockRatingRepository.findById(cik).ifPresent(rating -> {
            view.setTrend(rating.getTrend());
            view.setEfficiency(rating.getEfficiency());
            view.setOverall(rating.getOverall());
        });
        return Optional.of(view);
    }
}
