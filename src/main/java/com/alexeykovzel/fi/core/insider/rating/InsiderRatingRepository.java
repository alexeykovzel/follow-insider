package com.alexeykovzel.fi.core.insider.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsiderRatingRepository extends JpaRepository<InsiderRating, String> {
}
