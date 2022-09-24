package com.alexeykovzel.fi.core.insider;

import com.alexeykovzel.fi.core.trade.TradeRepository;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class InsiderService {
    private final TradeRepository tradeRepository;
    private final InsiderRatingRepository ratingRepository;
    private final InsiderRatingStrategy ratingStrategy;
    private final InsiderRepository repository;
    private final EntityManager entityManager;

    @Transactional
    public void saveInsider(Insider insider) {
        if (!repository.existsById(insider.getCik())) {
            repository.save(insider);
        }
    }

    @Transactional
    public void updateInsiderRating() {
        Collection<InsiderRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating insider rating...", repository.findAll(), insider ->
                ratings.add(InsiderRating.builder()
                        .efficiency(ratingStrategy.calculateEfficiency(insider))
                        .insider(insider)
                        .build())
        );
        ratingRepository.saveAll(ratings);
    }
}
