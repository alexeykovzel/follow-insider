package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.ModelHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StocksController {
    private final StockService stockService;

    // TODO: Handle errors like a normal human being...

    @GetMapping("/{symbol}")
    public ModelAndView getStockPage(@PathVariable String symbol, Model model) {
        return stockService.existsBySymbol(symbol)
                ? new ModelAndView("stock")
                : new ModelHandler(model).getErrorPage("400", "Stock Not Found",
                "Could not find a stock with such symbol: " + symbol);
    }

    @GetMapping("/{symbol}/xml")
    public StockView getStockXml(@PathVariable String symbol) {
        return stockService.getBySymbol(symbol).orElseThrow(() ->
                new IllegalArgumentException("Invalid stock symbol: " + symbol));
    }
}
