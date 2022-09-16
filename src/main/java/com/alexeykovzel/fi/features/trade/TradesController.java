package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.trade.view.TradeView;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trades")
@RequiredArgsConstructor
public class TradesController {
    private final TradeRepository tradeRepository;

    @GetMapping
    public Collection<TradeView> getRecentTrades(@RequestParam("type") List<String> type) {
        List<String> codes = type.stream().map(TradeType::codeOfValue).collect(Collectors.toList());
        return tradeRepository.findTop100ByCodeInOrderByDateDesc(codes);
    }
}
