package com.alexeykovzel.fi.config;

import com.alexeykovzel.fi.features.account.Authority;
import com.alexeykovzel.fi.features.account.User;
import com.alexeykovzel.fi.features.account.UserRepository;
import com.alexeykovzel.fi.features.stock.StockService;
import com.alexeykovzel.fi.features.trade.TradeService;
import com.alexeykovzel.fi.features.trade.form4.Form4Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DatabaseConfig {
    private final UserRepository userRepository;
    private final Form4Service form4Service;
    private final TradeService tradeService;
    private final StockService stockService;
    private final Environment environment;
    private final PasswordEncoder encoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equals("dev")) initDev();
            if (profile.equals("prod")) initProd();
        }

        // configure admin account
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User(adminEmail, encoder.encode(adminPassword), Authority.ADMIN.single());
            userRepository.save(admin);
        }
    }

    public void initDev() {
//        stockService.updateStocksLocally();
//        form4Service.updateRecentFilings(0, 20);

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
