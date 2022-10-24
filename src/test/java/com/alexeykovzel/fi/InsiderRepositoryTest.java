package com.alexeykovzel.fi;

import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.insider.InsiderRepository;
import com.alexeykovzel.fi.features.insider.InsiderView;
import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.stock.StockRepository;
import com.alexeykovzel.fi.features.trade.Trade;
import com.alexeykovzel.fi.features.trade.TradeCode;
import com.alexeykovzel.fi.features.trade.form4.Form4;
import com.alexeykovzel.fi.features.trade.form4.Form4Repository;
import com.alexeykovzel.fi.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(locations = "/application.yml")
public class InsiderRepositoryTest {

    @Autowired
    private InsiderRepository insiderRepository;

    @Autowired
    private Form4Repository form4Repository;

    @Autowired
    private StockRepository stockRepository;

    @Test
    @Transactional
    public void findInsiderViews() {
        // TODO: Fix test.
        Date currentDate = new Date();

        // create stock
        Stock stock = Stock.builder().cik("0").name("Test Stock").build();
        stockRepository.save(stock);

        // create trades
        Trade t1 = Trade.builder().code(TradeCode.PURCHASE).date(DateUtils.shiftDays(currentDate, -2)).leftShares(80.0).build();
        Trade t2 = Trade.builder().code(TradeCode.PURCHASE).date(DateUtils.shiftDays(currentDate, -1)).leftShares(140.0).build();
        Trade t3 = Trade.builder().code(TradeCode.PURCHASE).date(DateUtils.shiftDays(currentDate, -3)).leftShares(120.0).build();
        Trade t4 = Trade.builder().code(TradeCode.PURCHASE).date(DateUtils.shiftDays(currentDate, -4)).leftShares(100.0).build();
        Collection<Trade> trades = Set.of(t1, t2, t3, t4);

        // create insider
        Insider insider = new Insider("0", "John", stock, null, Set.of("CEO", "10% Owner"));

        // create form 4 and fill it with trades
        Form4 form4 = new Form4("0", trades, Set.of(insider), stock, null, null);
        trades.forEach(trade -> trade.setForm4(form4));
        form4Repository.save(form4);

        List<InsiderView> views = new ArrayList<>(insiderRepository.findViewsByStockSymbol(stock.getCik()));
        assertEquals(1, views.size());

        InsiderView view = views.get(0);
        assertEquals(insider.getName(), view.getName());
        assertEquals(String.join(", ", insider.getPositions()), view.getPositions());
        assertEquals(t2.getDate(), view.getLastActive());
        assertEquals(t2.getLeftShares(), view.getTotalShares());
    }
}
