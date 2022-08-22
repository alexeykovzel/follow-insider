package com.alexeykovzel.fi.features.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRatingRepository extends JpaRepository<CompanyRating, String> {
}
