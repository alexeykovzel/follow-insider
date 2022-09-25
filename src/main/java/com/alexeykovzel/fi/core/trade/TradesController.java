package com.alexeykovzel.fi.core.trade;

import com.alexeykovzel.fi.core.trade.view.TradeView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradesController {
    private final TradeService tradeService;

    @GetMapping("/recent")
    public Collection<TradeView> getRecentTrades(@RequestParam("types") List<String> types) {
        return tradeService.getRecentTradesByTypes(types);
    }
}
