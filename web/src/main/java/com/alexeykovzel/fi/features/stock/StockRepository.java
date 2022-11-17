package com.alexeykovzel.fi.features.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {

    @Query(value = "SELECT CONCAT(s.name, ' (', s.symbol, ')') FROM stocks s WHERE s.symbol != ''", nativeQuery = true)
    Collection<String> findFullNames();


    Stock findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
