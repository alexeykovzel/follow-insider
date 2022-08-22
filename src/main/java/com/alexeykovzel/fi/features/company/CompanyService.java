package com.alexeykovzel.fi.features.company;

import com.alexeykovzel.fi.features.EdgarService;
import com.alexeykovzel.fi.features.trade.TradeRating;
import com.alexeykovzel.fi.features.trade.TradeRatingRepository;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.utils.DateUtils;
import com.alexeykovzel.fi.utils.ProgressBar;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CompanyService extends EdgarService {
    private final CompanyRatingRepository companyRatingRepository;
    private final TradeRepository tradeRepository;
    private final TradeRatingRepository tradeRatingRepository;
    private final CompanyRepository companyRepository;

    // weights for different number of months after the transaction
    private static final Map<Integer, Double> TREND_WEIGHTS = Map.of(
            1, 0.6,
            2, 0.3,
            3, 0.1
    );

    /**
     * Updates data of all public companies stored in the SEC collection.
     */
    @Transactional
    public void updateCompanies() {
        ProgressBar.execute("Updating companies...", () -> {
            try {
                companyRepository.saveAll(getRemoteCompanies());
            } catch (IOException e) {
                System.out.println("[ERROR] Failed to update companies");
            }
        });
    }

    /**
     * Updates financial metrics of all public companies stored in the system.
     */
    @Transactional
    public void updateCompanyRating() {
        Collection<CompanyRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating company rating...", companyRepository.findAll(), company ->
                ratings.add(CompanyRating.builder()
                        .efficiency(calculateEfficiency(company))
                        .trend(calculateTrend(company))
                        .company(company)
                        .build()));
        companyRatingRepository.saveAll(ratings);
    }

    /**
     * Calculates the efficiency of company insiders as a number between 0 and 1, which indicates if an insider
     * performs better or worse than an average investor.
     *
     * @param company company which insiders are taken for evaluation
     * @return number between 0 and 1 that indicates the insiders' efficiency
     */
    public double calculateEfficiency(Company company) {
        double efficiency = 0;
        double totalWeight = 0;
        for (TradeRating rating : tradeRatingRepository.findByCompanyCik(company.getCik())) {
            efficiency += rating.getEfficiency() * rating.getWeight();
            totalWeight += rating.getWeight();
        }
        return (totalWeight != 0) ? (efficiency / totalWeight) : 0;
    }

    /**
     * Calculates the trend (bearish/bullish) of company insiders as a number between 0 and 1, which
     * indicates if the number of purchases changed comparing to historic values.
     *
     * @param company company which insiders are taken for evaluation
     * @return number between 0 and 1 that indicates the insiders' trend
     */
    public double calculateTrend(Company company) {
        String cik = company.getCik();
        Date minDate = tradeRepository.findMinDateByCik(cik);
        if (minDate == null) return 0;
        Date currentDate = new Date();
        double months = DateUtils.monthsBetween(minDate, currentDate);
        double average = tradeRepository.findPurchaseCount(cik) / months;
        double totalTrend = 0;

        for (int i = 1; i <= Math.min(months, TREND_WEIGHTS.size()); i++) {
            Date pastDate = DateUtils.shiftMonths(currentDate, -i);
            int count = tradeRepository.findPurchaseCount(cik, pastDate, currentDate);
            double trend = Math.min(1, (count - average) / average);
            totalTrend += trend * TREND_WEIGHTS.get(i);
        }
        return totalTrend;
    }

    private Collection<Company> getLocalCompanies() throws IOException {
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream resource = loader.getResourceAsStream("edgar/company-tickers.json");
        return getCompanies(new ObjectMapper().readTree(resource));
    }

    private Collection<Company> getRemoteCompanies() throws IOException {
        return getCompanies(getJsonByUrl(COMPANIES_URL));
    }

    private Collection<Company> getCompanies(JsonNode root) {
        Collection<Company> companies = new HashSet<>();
        try {
            root.get("data").forEach(item -> companies.add(Company.builder()
                    .cik(addLeadingZeros(item.get(0).asText()))
                    .name(normalize(item.get(1).asText()))
                    .symbol(item.get(2).asText())
                    .exchange(item.get(3).asText())
                    .build()));
        } catch (NullPointerException e) {
            System.out.println("[ERROR] Failed to access company data: " + e.getMessage());
        }
        return companies;
    }
}
