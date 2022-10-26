package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.trade.rating.TradeRating;
import com.alexeykovzel.fi.features.trade.rating.TradeRatingRepository;
import com.alexeykovzel.fi.features.trade.rating.TradeRatingStrategy;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRatingRepository tradeRatingRepository;
    private final TradeRatingStrategy ratingStrategy;
    private final TradeRepository tradeRepository;

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
}
