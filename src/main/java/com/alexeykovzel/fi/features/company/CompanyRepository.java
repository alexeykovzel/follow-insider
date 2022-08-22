package com.alexeykovzel.fi.features.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface CompanyRepository extends JpaRepository<Company, String> {

    @Query("SELECT c.cik FROM Company c")
    Collection<String> getStoredCompanies();

    @Query("SELECT c FROM Company c WHERE NOT EXISTS (SELECT 1 FROM StockRecord r WHERE c = r.company)")
    Collection<Company> findWithoutStockRecords();

    @Query(value = "SELECT CONCAT(c.name, ' (', c.symbol, ')') FROM companies c WHERE c.symbol != ''", nativeQuery = true)
    Collection<String> findAllNames();

    Company findBySymbol(String symbol);
}
