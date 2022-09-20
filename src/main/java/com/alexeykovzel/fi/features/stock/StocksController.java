package com.alexeykovzel.fi.features.stock;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/stocks")
@RequiredArgsConstructor
public class StocksController {
    private final StockService stockService;

    @GetMapping("/{symbol}")
    public StockView getStockBySymbol(@PathVariable String symbol) {
        return stockService.getStockViewBySymbol(symbol).orElseThrow(() ->
                new IllegalArgumentException("Invalid stock symbol: " + symbol));
    }
}
