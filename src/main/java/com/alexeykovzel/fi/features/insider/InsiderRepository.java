package com.alexeykovzel.fi.features.insider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface InsiderRepository extends JpaRepository<Insider, String> {

    @Query("SELECT i.name FROM Insider i")
    Collection<String> findAllNames();
}
