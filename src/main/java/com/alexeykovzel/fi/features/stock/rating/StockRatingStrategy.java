package com.alexeykovzel.fi.features.stock.rating;

import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.trade.rating.TradeRating;
import com.alexeykovzel.fi.features.trade.rating.TradeRatingRepository;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockRatingStrategy {
    private final TradeRatingRepository tradeRatingRepository;
    private final TradeRepository tradeRepository;

    // weights for different number of months after the transaction
    public static final Map<Integer, Double> TREND_WEIGHTS = Map.of(
            1, 0.6,
            2, 0.3,
            3, 0.1
    );

    /**
     * Calculates the efficiency of stock insiders as a number between 0 and 1, which indicates if an insider
     * performs better or worse than an average investor.
     *
     * @param stock stock which insiders are taken for evaluation
     * @return number between 0 and 1 that indicates the insiders' efficiency
     */
    public double calculateEfficiency(Stock stock) {
        double efficiency = 0;
        double totalWeight = 0;
        for (TradeRating rating : tradeRatingRepository.findByStockCik(stock.getCik())) {
            efficiency += rating.getEfficiency() * rating.getWeight();
            totalWeight += rating.getWeight();
        }
        return (totalWeight != 0) ? (efficiency / totalWeight) : 0;
    }

    /**
     * Calculates the trend (bearish/bullish) of stock insiders as a number between 0 and 1, which
     * indicates if the number of purchases changed comparing to historic values.
     *
     * @param stock stock which insiders are taken for evaluation
     * @return number between 0 and 1 that indicates the insiders' trend
     */
    public double calculateTrend(Stock stock) {
        String cik = stock.getCik();
        Date minDate = tradeRepository.findMinDateByCik(cik);
        if (minDate == null) return 0;
        Date currentDate = new Date();
        double months = DateUtils.monthsBetween(minDate, currentDate);
        double average = tradeRepository.findBuyCountByCik(cik) / months;
        double totalTrend = 0;

        for (int i = 1; i <= Math.min(months, TREND_WEIGHTS.size()); i++) {
            Date pastDate = DateUtils.shiftMonths(currentDate, -i);
            int count = tradeRepository.findBuyCountByCik(cik, pastDate, currentDate);
            double trend = Math.min(1, (count - average) / average);
            totalTrend += trend * TREND_WEIGHTS.get(i);
        }
        return totalTrend;
    }
}
