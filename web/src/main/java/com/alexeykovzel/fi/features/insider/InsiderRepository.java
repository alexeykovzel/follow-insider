package com.alexeykovzel.fi.features.insider;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface InsiderRepository extends JpaRepository<Insider, String> {

    @Query(value = "SELECT DISTINCT ON (i.cik) i.name, string_agg(ip.positions, ', ') AS positions, " +
            "t.date AS lastActive, t.left_shares AS totalShares " +
            "FROM insiders i, insider_positions ip, trades t, form4s f, stocks s, " +
            "       (SELECT i0.cik insider_cik, MAX(t0.date) date" +
            "         FROM insiders i0, trades t0, form4s f0" +
            "         WHERE f0.accession_no = t0.accession_no " +
            "           AND f0.stock_cik = i0.stock_cik " +
            "         GROUP BY i0.cik) last_trades " +
            "WHERE i.cik = ip.insider_cik " +
            "  AND s.symbol = :symbol " +
            "  AND s.cik = f.stock_cik " +
            "  AND f.stock_cik = i.stock_cik " +
            "  AND t.accession_no = f.accession_no " +
            "  AND i.cik = last_trades.insider_cik " +
            "  AND t.date = last_trades.date " +
            "GROUP BY i.cik, lastActive, totalShares",
            nativeQuery = true)
    Collection<InsiderView> findViewsByStockSymbol(String symbol);
}
