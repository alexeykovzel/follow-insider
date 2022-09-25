package com.alexeykovzel.fi.core.stock;

import com.alexeykovzel.fi.core.ModelHandler;
import com.alexeykovzel.fi.core.insider.InsiderRepository;
import com.alexeykovzel.fi.core.insider.InsiderView;
import com.alexeykovzel.fi.core.trade.TradeRepository;
import com.alexeykovzel.fi.core.trade.TradeService;
import com.alexeykovzel.fi.core.trade.view.TradeView;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StocksController {
    private final InsiderRepository insiderRepository;
    private final StockRepository stockRepository;
    private final TradeService tradeService;
    private final StockService stockService;

    @GetMapping("/all")
    public Collection<StockView> getAllStocksInfo() {
        List<StockView> views = new ArrayList<>();
        for (Stock stock : stockRepository.findAll()) {
            stockService.getStockView(stock).ifPresent(views::add);
        }
        return views;
    }

    @GetMapping("/{symbol}")
    public ModelAndView getStockPage(@PathVariable String symbol, Model model) {
        return stockRepository.existsBySymbol(symbol.toUpperCase())
                ? new ModelAndView("stock")
                : new ModelHandler(model).getErrorPage("400", "Stock Not Found",
                "Could not find a stock with such symbol: " + symbol);
    }

    @GetMapping("/{symbol}/info")
    public StockView getStockInfo(@PathVariable String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase());
        return stockService.getStockView(stock).orElseThrow(() ->
                new IllegalArgumentException("Invalid stock symbol: " + symbol));
    }

    @GetMapping("/{symbol}/trades")
    public Collection<TradeView> getStockTrades(@PathVariable String symbol,
                                                @RequestParam("types") List<String> types) {
        return tradeService.getTradesByStockSymbol(symbol, types);
    }

    @GetMapping("/{symbol}/insiders")
    public Collection<InsiderView> getStockInsiders(@PathVariable String symbol) {
        return insiderRepository.findViewsByStockSymbol(symbol);
    }
}
