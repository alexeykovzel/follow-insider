package com.alexeykovzel.fi.config;

import com.alexeykovzel.fi.features.stock.StockService;
import com.alexeykovzel.fi.features.trade.TradeService;
import com.alexeykovzel.fi.features.trade.form4.Form4Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DatabaseConfig {
    private final Form4Service form4Service;
    private final TradeService tradeService;
    private final StockService stockService;
    private final Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equals("dev")) initDev();
            if (profile.equals("prod")) initProd();
        }
    }

    public void initDev() {
        stockService.updateStocksLocally();
        form4Service.updateRecentFilings(0, 20);

//        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
//        executor.scheduleAtFixedRate(() -> form4Service.updateRecentFilings(0, 40), 0, 2, TimeUnit.MINUTES);
    }

    public void initProd() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        // update public stocks every 3 days
        executor.scheduleAtFixedRate(stockService::updateStocksRemotely, 0, 3, TimeUnit.DAYS);

        // load recent trades every 30 seconds
        executor.scheduleAtFixedRate(() -> form4Service.updateRecentFilings(0, 40), 0, 30, TimeUnit.SECONDS);

        // update stock prices every day
        executor.scheduleAtFixedRate(stockService::updateStockRecords, 0, 1, TimeUnit.DAYS);

        // update ratings every day
        executor.scheduleAtFixedRate(() -> {
            tradeService.updateTradeRating();
            stockService.updateStockRating();
        }, 0, 1, TimeUnit.DAYS);
    }
}
