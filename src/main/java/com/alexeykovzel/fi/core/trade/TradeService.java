package com.alexeykovzel.fi.core.trade;

import com.alexeykovzel.fi.core.trade.view.TradeView;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final TradeRatingRepository tradeRatingRepository;
    private final TradeRatingStrategy ratingStrategy;

    @Transactional
    public void updateTradeRating() {
        Collection<TradeRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating trade rating...", tradeRepository.findByCodeWhereNoRating("P"), trade ->
                ratings.add(TradeRating.builder()
                        .efficiency(ratingStrategy.calculateEfficiency(trade))
                        .weight(ratingStrategy.calculateWeight(trade))
                        .trade(trade)
                        .build()));
        tradeRatingRepository.saveAll(ratings);
    }

    public Collection<TradeView> getRecentTradesByTypes(List<String> types) {
        return tradeRepository.findTop100ByCodeInOrderByDateDesc(getCodesByTypes(types));
    }

    public Collection<TradeView> getTradesByStockSymbol(String symbol, List<String> types) {
        return tradeRepository.findByStockSymbol(symbol, getCodesByTypes(types));
    }
    
    private List<String> getCodesByTypes(List<String> types) {
        List<String> codes = new ArrayList<>();
        for (String type : types) {
            String code = TradeCode.codeOfType(type);
            codes.add(code);
        }
        return codes;
    }
}
