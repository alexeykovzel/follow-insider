package com.alexeykovzel.fi.features.insider.rating;

import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.trade.rating.TradeRating;
import com.alexeykovzel.fi.features.trade.rating.TradeRatingRepository;
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
