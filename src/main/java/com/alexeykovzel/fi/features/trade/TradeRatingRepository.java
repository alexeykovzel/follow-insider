package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.insider.Insider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TradeRatingRepository extends JpaRepository<TradeRating, Long> {

    @Query("SELECT r FROM TradeRating r WHERE :insider MEMBER OF r.trade.form4.insiders")
    Collection<TradeRating> findByInsider(Insider insider);

    @Query("SELECT r FROM TradeRating r WHERE r.trade.form4.stock.cik = :cik")
    Collection<TradeRating> findByStockCik(String cik);
}
