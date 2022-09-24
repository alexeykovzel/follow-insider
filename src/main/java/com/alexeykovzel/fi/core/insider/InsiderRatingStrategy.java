package com.alexeykovzel.fi.core.insider;

import com.alexeykovzel.fi.core.trade.TradeRating;
import com.alexeykovzel.fi.core.trade.TradeRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsiderRatingStrategy {
    private final TradeRatingRepository tradeRatingRepository;

    public double calculateEfficiency(Insider insider) {
        double efficiency = 0;
        double totalWeight = 0;
        for (TradeRating rating : tradeRatingRepository.findByInsider(insider)) {
            efficiency += rating.getEfficiency() * rating.getWeight();
            totalWeight += rating.getWeight();
        }
        return (totalWeight != 0) ? (efficiency / totalWeight) : 0;
    }
}
