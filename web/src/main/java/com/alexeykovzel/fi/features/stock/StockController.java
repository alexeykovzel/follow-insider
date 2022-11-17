package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.insider.InsiderRepository;
import com.alexeykovzel.fi.features.insider.InsiderView;
import com.alexeykovzel.fi.features.trade.TradeCode;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.features.trade.view.TradePoint;
import com.alexeykovzel.fi.features.trade.view.TradeView;
import com.alexeykovzel.fi.common.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {
    private final InsiderRepository insiderRepository;
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;
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
    public ModelAndView getStockPage(@PathVariable String symbol) {
        if (stockRepository.existsBySymbol(symbol.toUpperCase())) {
            return new ModelAndView("stock");
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stock: " + symbol);
    }

    @GetMapping("/{symbol}/info")
    public StockView getStockInfo(@PathVariable String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase());
        return stockService.getStockView(stock).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stock: " + symbol));
    }

    @GetMapping("/{symbol}/trades")
    public Collection<TradeView> getStockTrades(@PathVariable String symbol,
                                                @RequestParam(value = "types", required = false) List<String> types) {
        return tradeRepository.findViewBySymbol(symbol, TradeCode.ofTypes(types));
    }

    @GetMapping("/{symbol}/insiders")
    public Collection<InsiderView> getStockInsiders(@PathVariable String symbol) {
        return insiderRepository.findViewsByStockSymbol(symbol);
    }

    @GetMapping("/{symbol}/trade-points")
    public Collection<TradePoint> getTradeGraph(@PathVariable("symbol") String symbol,
                                                @RequestParam("range") String range,
                                                @RequestParam(value = "types", required = false) List<String> types) {
        Date from = DateUtils.shiftRange(new Date(), range);
        return tradeRepository.findPointsBySymbol(symbol, TradeCode.ofTypes(types), from);
    }
}