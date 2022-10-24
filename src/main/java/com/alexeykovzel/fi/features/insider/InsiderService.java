package com.alexeykovzel.fi.features.insider;

import com.alexeykovzel.fi.features.insider.rating.InsiderRating;
import com.alexeykovzel.fi.features.insider.rating.InsiderRatingRepository;
import com.alexeykovzel.fi.features.insider.rating.InsiderRatingStrategy;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsiderService {
    private final InsiderRatingRepository ratingRepository;
    private final InsiderRatingStrategy ratingStrategy;
    private final InsiderRepository insiderRepository;

    @Transactional
    public void updateInsiderRating() {
        Collection<InsiderRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating insider rating...", insiderRepository.findAll(),
                insider -> ratings.add(InsiderRating.builder()
                        .efficiency(ratingStrategy.calculateEfficiency(insider))
                        .insider(insider)
                        .build()));
        ratingRepository.saveAll(ratings);
    }
}
