package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.trade.view.TradeView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/rest/trades")
@RequiredArgsConstructor
public class TradesController {
    private final TradeService tradeService;

    @GetMapping
    public Collection<TradeView> getRecentTrades(@RequestParam("type") List<String> type) {
        return tradeService.getRecentTradesByType(type);
    }
}
