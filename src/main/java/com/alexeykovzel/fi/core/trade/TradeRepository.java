package com.alexeykovzel.fi.core.trade;

import com.alexeykovzel.fi.core.trade.view.TradePoint;
import com.alexeykovzel.fi.core.trade.view.TradeView;
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
    Slice<TradeView> findRecent(Collection<String> codes, Pageable paging);

    @Query(value = "SELECT t FROM Trade t")
    Slice<TradeView> findRecent(Pageable paging);

    @Query("SELECT t FROM Trade t WHERE NOT EXISTS (SELECT 1 FROM TradeRating r WHERE t = r.trade) AND t.code = :code")
    Collection<Trade> findNotRated(String code);

    @Query("SELECT t FROM Trade t WHERE t.form4.stock.symbol = :symbol AND t.code IN (:codes) AND t.date > :from")
    Collection<TradePoint> findPointsBySymbol(String symbol, Collection<String> codes, Date from);

    @Query("SELECT t FROM Trade t WHERE t.form4.stock.symbol = :symbol AND t.code IN (:codes)")
    Collection<TradeView> findBySymbol(String symbol, Collection<String> codes);

    @Query("SELECT t FROM Trade t WHERE t.form4.stock.symbol = :symbol")
    Collection<TradeView> findBySymbol(String symbol);

    @Query("SELECT c.symbol FROM Trade t, Stock c WHERE t.id = :id AND c=t.form4.stock")
    String findSymbolById(Long id);

    @Query("SELECT MIN(t.date) FROM Trade t WHERE t.form4.stock.cik = :cik")
    Date findMinDateByCik(String cik);

    @Query("SELECT MAX(t.date) FROM Trade t WHERE t.form4.stock.cik = :cik")
    Date findMaxDateByCik(String cik);

    @Query("SELECT COUNT(t) FROM Trade t WHERE t.form4.stock.cik = :cik AND t.code = 'P' AND t.date >= :d1 AND t.date <= :d2")
    int findBuyCountByCik(String cik, Date d1, Date d2);

    @Query("SELECT COUNT(t) FROM Trade t WHERE t.form4.stock.cik = :cik AND t.code = 'P'")
    int findBuyCountByCik(String cik);

    @Query("SELECT AVG(t.shareCount) FROM Trade t WHERE t.code = 'P'")
    double findAvgBoughtShares();
}
