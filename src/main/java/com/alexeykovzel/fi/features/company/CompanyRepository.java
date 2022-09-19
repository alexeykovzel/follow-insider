package com.alexeykovzel.fi.features.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query(value = "SELECT CONCAT(c.name, ' (', c.symbol, ')') FROM companies c WHERE c.symbol != ''", nativeQuery = true)
    Collection<String> findAllNames();

    Company findBySymbol(String symbol);

    @Query("SELECT c.symbol FROM Company c WHERE c.name = :name")
    String findSymbolByName(String name);
}
