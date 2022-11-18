package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.insider.Insider;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    @Query(value = "SELECT t FROM Trade t WHERE t.code IN (:codes)")
    Slice<Trade.View> findPagingViews(Pageable paging, Collection<TradeCode> codes);

    @Query("SELECT t FROM Trade t WHERE t.form4.stock.symbol = :symbol AND t.code IN (:codes)")
    Collection<Trade.View> findViewsBySymbol(String symbol, Collection<TradeCode> codes);

    @Query(value = "SELECT t FROM Trade t WHERE t.date > :from AND t.code = 'P' AND t.form4.stock.cik = :cik")
    Collection<Trade> findRecentBuysBuyCik(String cik, Date from);

    @Query("SELECT t FROM Trade t WHERE NOT EXISTS (SELECT 1 FROM TradeRating r WHERE t = r.trade) AND t.code = :code")
    Collection<Trade> findNotRated(String code);

    @Query("SELECT t FROM Trade t WHERE t.form4.stock.symbol = :symbol AND t.code IN (:codes) AND t.date > :from")
    Collection<Trade.Point> findPointsByStock(String symbol, Collection<TradeCode> codes, Date from);

    @Query("SELECT c.symbol FROM Trade t, Stock c WHERE t.id = :id AND c=t.form4.stock")
    String findSymbolById(Long id);

    @Query("SELECT MIN(t.date) FROM Trade t WHERE t.form4.stock.cik = :cik")
    Date findMinDateByStock(String cik);

    @Query("SELECT MAX(t.date) FROM Trade t WHERE t.form4.stock.cik = :cik")
    Date findMaxDateByStock(String cik);

    @Query("SELECT MAX(t.date) FROM Trade t WHERE :insider IN t.form4.insiders")
    Date findMaxDateByInsider(Insider insider);

    @Query("SELECT COUNT(t) FROM Trade t WHERE t.form4.stock.cik = :cik AND t.code = 'P' AND t.date >= :d1 AND t.date <= :d2")
    int findBuyCountByStock(String cik, Date d1, Date d2);

    @Query("SELECT COUNT(t) FROM Trade t WHERE t.form4.stock.cik = :cik AND t.code = 'P'")
    int findBuyCountByStock(String cik);

    @Query("SELECT AVG(t.shareCount) FROM Trade t WHERE t.code = 'P'")
    double findAvgBoughtShares();
}
