package com.alexeykovzel.fi.config;

import com.alexeykovzel.fi.features.stock.StockService;
import com.alexeykovzel.fi.features.form4.Form4Service;
import com.alexeykovzel.fi.features.insider.InsiderService;
import com.alexeykovzel.fi.features.trade.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DatabaseConfig {
    private final StockService stockService;
    private final InsiderService insiderService;
    private final TradeService tradeService;
    private final Form4Service form4Service;

    @PostConstruct
    public void init() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(stockService::updateStocks, 0, 1, TimeUnit.DAYS);
        executor.scheduleAtFixedRate(() -> form4Service.updateRecentFilings(0, 40), 0, 30, TimeUnit.SECONDS);

//        executor.scheduleAtFixedRate(() -> {
//            tradeService.updateTradeRating();
//            insiderService.updateInsiderRating();
//            companyService.updateCompanyRating();
//            stockService.updateStockRecords();
//        }, 0, 1, TimeUnit.DAYS);
    }
}
