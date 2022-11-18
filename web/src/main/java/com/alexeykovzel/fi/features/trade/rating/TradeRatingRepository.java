package com.alexeykovzel.fi.features.trade.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TradeRatingRepository extends JpaRepository<TradeRating, Long> {

    @Query("SELECT r FROM TradeRating r WHERE r.trade.form4.stock.cik = :cik")
    Collection<TradeRating> findByStock(String cik);
}
