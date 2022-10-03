package com.alexeykovzel.fi.config;

import com.alexeykovzel.fi.core.insider.InsiderService;
import com.alexeykovzel.fi.core.stock.StockService;
import com.alexeykovzel.fi.core.trade.TradeService;
import com.alexeykovzel.fi.core.trade.form4.Form4Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DatabaseConfig {
    private final InsiderService insiderService;
    private final Form4Service form4Service;
    private final TradeService tradeService;
    private final StockService stockService;

    @PostConstruct
    public void init() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        // update public stocks every day
        executor.scheduleAtFixedRate(stockService::updateStocksRemotely, 1, 1, TimeUnit.DAYS);

        // TODO: Change back to recent filings.
        // load recent filings every 30 seconds
        executor.scheduleAtFixedRate(() -> form4Service.updateFilings("INTC"), 0, 30, TimeUnit.SECONDS);

        // update stock prices every day
//        executor.scheduleAtFixedRate(stockService::updateStockRecords, 0, 1, TimeUnit.DAYS);

        // update ratings every day
//        executor.scheduleAtFixedRate(() -> {
//            tradeService.updateTradeRating();
//            insiderService.updateInsiderRating();
//            stockService.updateStockRating();
//        }, 0, 1, TimeUnit.DAYS);
    }
}
