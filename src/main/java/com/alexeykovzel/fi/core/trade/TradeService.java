package com.alexeykovzel.fi.core.trade;

import com.alexeykovzel.fi.core.trade.rating.TradeRating;
import com.alexeykovzel.fi.core.trade.rating.TradeRatingRepository;
import com.alexeykovzel.fi.core.trade.rating.TradeRatingStrategy;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
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
        ProgressBar.execute("Updating trade rating...", tradeRepository.findNotRated("P"), trade ->
                ratings.add(TradeRating.builder()
                        .efficiency(ratingStrategy.calculateEfficiency(trade))
                        .weight(ratingStrategy.calculateWeight(trade))
                        .trade(trade)
                        .build()));
        tradeRatingRepository.saveAll(ratings);
    }

    public List<String> getCodesByTypes(List<String> types) {
        return types.stream()
                .map(TradeCode::codeOfType)
                .collect(Collectors.toList());
    }
}
