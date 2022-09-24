package com.alexeykovzel.fi.config;

import com.alexeykovzel.fi.core.insider.InsiderRepository;
import com.alexeykovzel.fi.core.stock.StockService;
import com.alexeykovzel.fi.core.trade.form4.Form4Service;
import com.alexeykovzel.fi.core.insider.InsiderService;
import com.alexeykovzel.fi.core.trade.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DatabaseConfig {
    private final StockService stockService;
    private final InsiderService insiderService;
    private final TradeService tradeService;
    private final InsiderRepository insiderRepository;
    private final Form4Service form4Service;
    private final DataSource dataSource;

    @PostConstruct
    public void init() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(stockService::updateStocksRemotely, 0, 1, TimeUnit.DAYS);
        executor.scheduleAtFixedRate(() -> form4Service.updateRecentFilings(0, 40), 0, 30, TimeUnit.SECONDS);

//        executor.scheduleAtFixedRate(() -> {
//            tradeService.updateTradeRating();
//            insiderService.updateInsiderRating();
//            companyService.updateCompanyRating();
//            stockService.updateStockRecords();
//        }, 0, 1, TimeUnit.DAYS);
    }
}
