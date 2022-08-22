package com.alexeykovzel.fi.features.insider;

import com.alexeykovzel.fi.features.trade.TradeRating;
import com.alexeykovzel.fi.features.trade.TradeRatingRepository;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class InsiderService {
    private final InsiderRatingRepository insiderRatingRepository;
    private final TradeRatingRepository tradeRatingRepository;
    private final InsiderRepository insiderRepository;

    @Transactional
    public void saveInsider(Insider insider) {
        if (!insiderRepository.existsById(insider.getCik())) {
            insiderRepository.save(insider);
        }
    }

    @Transactional
    public void updateInsiderRating() {
        Collection<InsiderRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating insider rating...", insiderRepository.findAll(), insider ->
                ratings.add(InsiderRating.builder()
                        .efficiency(calculateEfficiency(insider))
                        .insider(insider)
                        .build()));
        insiderRatingRepository.saveAll(ratings);
    }

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
