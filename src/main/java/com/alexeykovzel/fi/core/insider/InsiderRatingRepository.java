package com.alexeykovzel.fi.core.insider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsiderRatingRepository extends JpaRepository<InsiderRating, String> {
}
