package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.insider.InsiderRepository;
import com.alexeykovzel.fi.features.trade.Trade;
import com.alexeykovzel.fi.features.trade.TradeCode;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.common.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
@RequestMapping("/stocks")
@RequiredArgsConstructor
public class StockController {
    private final InsiderRepository insiderRepository;
    private final TradeRepository tradeRepository;
    private final StockRepository stockRepository;

    @RequestMapping("/{symbol}")
    public ModelAndView getStockPage(@PathVariable String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol.toUpperCase());
        if (stock == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stock: " + symbol);
        return new ModelAndView("stock");
    }

    @GetMapping("/all")
    public Collection<Stock.View> getTableViews() {
        return stockRepository.findAllViews();
    }

    @GetMapping("/{symbol}/info")
    public Stock.View getStockInfo(@PathVariable String symbol) {
        Stock.View view = stockRepository.findViewBySymbol(symbol.toUpperCase());
        if (view == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid stock: " + symbol);
        return view;
    }

    @GetMapping("/{symbol}/insiders")
    public Collection<Insider.TableView> getStockInsiders(@PathVariable String symbol) {
        return insiderRepository.findTableViewsByStock(symbol.toUpperCase());
    }

    @GetMapping("/{symbol}/trades")
    public Collection<Trade.View> getStockTrades(@PathVariable(value = "symbol") String symbol,
                                                 @RequestParam(value = "types", required = false) List<String> types) {
        return tradeRepository.findViewsBySymbol(symbol.toUpperCase(), TradeCode.ofTypes(types));
    }

    @GetMapping("/{symbol}/trades/points")
    public Collection<Trade.Point> getTradePoints(@PathVariable(value = "symbol") String symbol,
                                                  @RequestParam(value = "range") String range,
                                                  @RequestParam(value = "types", required = false) List<String> types) {
        Date from = DateUtils.shiftRange(new Date(), range);
        return tradeRepository.findPointsByStock(symbol.toUpperCase(), TradeCode.ofTypes(types), from);
    }
}