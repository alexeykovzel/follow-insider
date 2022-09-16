package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.trade.view.TradeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    Collection<TradeView> findTop100ByCodeInOrderByDateDesc(List<String> codes);

    @Query("SELECT t FROM Trade t WHERE NOT EXISTS (SELECT 1 FROM TradeRating r WHERE t = r.trade) AND t.code = :code")
    Collection<Trade> findByCodeWhereNoRating(String code);

    @Query("SELECT COUNT(t) FROM Trade t WHERE t.date >= :d1 AND t.date <= :d2 AND t.code = 'P' AND t.form4.company.cik = :cik")
    int findPurchaseCount(String cik, Date d1, Date d2);

    @Query("SELECT COUNT(t) FROM Trade t WHERE t.form4.company.cik = :cik AND t.code = 'P'")
    int findPurchaseCount(String cik);

    @Query("SELECT AVG(t.shareCount) FROM Trade t WHERE t.code = 'P'")
    double findAveragePurchasedShares();

    @Query("SELECT c.symbol FROM Trade t, Company c WHERE t.id = :id AND c=t.form4.company")
    String findSymbolById(Long id);

    @Query("SELECT MIN(t.date) FROM Trade t WHERE t.form4.company.cik = :cik")
    Date findMinDateByCik(String cik);
}
