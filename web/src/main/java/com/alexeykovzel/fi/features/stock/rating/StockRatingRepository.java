package com.alexeykovzel.fi.features.stock.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRatingRepository extends JpaRepository<StockRating, String> {
}
