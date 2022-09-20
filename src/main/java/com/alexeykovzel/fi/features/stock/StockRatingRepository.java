package com.alexeykovzel.fi.features.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRatingRepository extends JpaRepository<StockRating, String> {

    @Query("SELECT r FROM StockRating r WHERE r.stock.symbol = :symbol")
    StockRating findBySymbol(String symbol);
}
