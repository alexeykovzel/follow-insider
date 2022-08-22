package com.alexeykovzel.fi.features.insider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsiderRatingRepository extends JpaRepository<InsiderRating, String> {
}
