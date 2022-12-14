package com.alexeykovzel.fi.features.stock.records;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface StockRecordRepository extends JpaRepository<StockRecord, Long> {
    String CLOSEST_DATE = "SELECT s.date FROM stock_records s WHERE s.date < :date ORDER BY s.date DESC LIMIT 1";

    @Query("SELECT COUNT(s.dividends) FROM StockRecord s WHERE (s.date > :sDate) AND (s.date < :eDate) AND (s.stock.symbol = :symbol) GROUP BY s.stock.symbol")
    double findDividendsBetween(String symbol, Date sDate, Date eDate);

    @Query(value = "SELECT s.price FROM stock_records s WHERE s.date = (" + CLOSEST_DATE + ") LIMIT 1", nativeQuery = true)
    double findNearestPrice(Date date);
}