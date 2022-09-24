package com.alexeykovzel.fi.core.stock;

import com.alexeykovzel.fi.core.ModelHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StocksController {
    private final StockService stockService;

    // TODO: Handle errors like a normal human being...

    @GetMapping("/xml")
    public List<StockView> getAllStocks() {
        return stockService.getAll();
    }

    @GetMapping("/{symbol}/xml")
    public StockView getStock(@PathVariable String symbol) {
        return stockService.getBySymbol(symbol).orElseThrow(() ->
                new IllegalArgumentException("Invalid stock symbol: " + symbol));
    }

    @GetMapping("/{symbol}")
    public ModelAndView getStockPage(@PathVariable String symbol, Model model) {
        return stockService.existsBySymbol(symbol)
                ? new ModelAndView("stock")
                : new ModelHandler(model).getErrorPage("400", "Stock Not Found",
                "Could not find a stock with such symbol: " + symbol);
    }
}