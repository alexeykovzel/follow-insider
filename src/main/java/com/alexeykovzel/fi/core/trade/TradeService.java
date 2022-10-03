package com.alexeykovzel.fi.core.trade;

import com.alexeykovzel.fi.core.trade.view.TradeView;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final TradeRatingRepository tradeRatingRepository;
    private final TradeRatingStrategy ratingStrategy;

    @Transactional
    public void updateTradeRating() {
        Collection<TradeRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating trade rating...", tradeRepository.findByCodeWithNoRating("P"), trade ->
                ratings.add(TradeRating.builder()
                        .efficiency(ratingStrategy.calculateEfficiency(trade))
                        .weight(ratingStrategy.calculateWeight(trade))
                        .trade(trade)
                        .build()));
        tradeRatingRepository.saveAll(ratings);
    }

    public Collection<TradeView> getRecentTradesByTypes(List<String> types) {
        Pageable paging = PageRequest.of(0, 100, Sort.by("date").descending());
        if (types == null || types.isEmpty()) return tradeRepository.findRecentTrades(paging).getContent();
        return tradeRepository.findRecentTrades(getCodesByTypes(types), paging).getContent();
    }

    public Collection<TradeView> getTradesByStockSymbol(String symbol, List<String> types) {
        if (types == null || types.isEmpty()) return tradeRepository.findByStockSymbol(symbol);
        return tradeRepository.findByStockSymbol(symbol, getCodesByTypes(types));
    }

    private List<String> getCodesByTypes(List<String> types) {
        return types.stream()
                .map(TradeCode::codeOfType)
                .collect(Collectors.toList());
    }
}
