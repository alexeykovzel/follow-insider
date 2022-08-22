package com.alexeykovzel.fi.features.trade;

import com.alexeykovzel.fi.features.stock.StockRecordRepository;
import com.alexeykovzel.fi.utils.DateUtils;
import com.alexeykovzel.fi.utils.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final TradeRepository tradeRepository;
    private final TradeRatingRepository tradeRatingRepository;
    private final StockRecordRepository stockRecordRepository;

    private static final double QSCORE_WEIGHT = 0.8;
    private static final double QSCORE_BASE = 0.5;

    private static final Map<Integer, Integer> ROI_WEIGHTS = Map.of(
            1, 1,
            3, 4,
            6, 6,
            12, 4,
            36, 2,
            60, 1
    );

    @Transactional
    public void updateTradeRating() {
        Collection<TradeRating> ratings = new HashSet<>();
        ProgressBar.execute("Updating trade rating...", tradeRepository.findByCodeWhereNoRating("P"), trade ->
                ratings.add(TradeRating.builder()
                        .efficiency(calculateEfficiency(trade))
                        .weight(calculateWeight(trade))
                        .trade(trade)
                        .build()));
        tradeRatingRepository.saveAll(ratings);
    }

    public double calculateWeight(Trade trade) {
        double quantity = trade.getShareCount();
        double leftShares = trade.getLeftShares();
        double averageQuantity = tradeRepository.findAveragePurchasedShares();
        double qScore = Math.pow(QSCORE_BASE, averageQuantity / quantity);
        return qScore * QSCORE_WEIGHT + (quantity / leftShares) * (1 - QSCORE_WEIGHT);
    }

    public double calculateEfficiency(Trade trade) {
        Date currentDate = new Date();
        double totalRoi = 0;
        double totalWeight = 0;
        for (Map.Entry<Integer, Integer> weight : ROI_WEIGHTS.entrySet()) {
            // stop if records exceed the current date
            Date recordDate = DateUtils.shiftMonths(trade.getDate(), weight.getKey());
            if (currentDate.before(recordDate)) break;
            // otherwise calculate ROI and increase total weight
            totalRoi += calculateAnnualisedRoi(trade, recordDate, weight.getKey()) * weight.getValue();
            totalWeight += weight.getValue();
        }
        return (totalWeight != 0) ? (totalRoi / totalWeight) : 0;
    }

    public double calculateAnnualisedRoi(Trade trade, Date endDate, int months) {
        String symbol = tradeRepository.findSymbolById(trade.getId());
        double price = stockRecordRepository.findNearestPrice(endDate);
        double dividends = stockRecordRepository.findDividendsBetween(symbol, trade.getDate(), endDate);
        double roi = calculateRoi(price, trade.getSharePrice(), trade.getShareCount(), dividends);
        return calculateAnnualisedRoi(roi, months);
    }

    private double calculateAnnualisedRoi(double roi, double months) {
        return Math.pow(1 + roi, 12 / months) - 1;
    }

    private double calculateRoi(double price, double entryPoint, double quantity, double dividends) {
        return ((price - entryPoint) * quantity + dividends) / (entryPoint * quantity);
    }
}
