package com.alexeykovzel.fi.features.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface StockRecordRepository extends JpaRepository<StockRecord, Long> {

    @Query("SELECT COUNT(s.dividends) FROM StockRecord s WHERE s.date > :sDate AND s.date < :eDate AND s.company.symbol = :symbol GROUP BY s.company.symbol")
    double findDividendsBetween(@Param("symbol") String symbol, @Param("sDate") Date startDate, @Param("eDate") Date endDate);

    @Query(value = "SELECT s.price FROM stock_records s WHERE s.date = (SELECT s.date FROM stock_records s WHERE s.date < :date ORDER BY s.date DESC LIMIT 1) LIMIT 1", nativeQuery = true)
    double findNearestPrice(@Param("date") Date date);
}