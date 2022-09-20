package com.alexeykovzel.fi.company;

import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.stock.StockService;
import com.alexeykovzel.fi.features.trade.TradeRating;
import com.alexeykovzel.fi.features.trade.TradeRatingRepository;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private TradeRatingRepository tradeRatingRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    public void givenStock_whenCalculateTrend_thenSuccess() {
        when(tradeRepository.findPurchaseCountByCik(any())).thenReturn(30);
        when(tradeRepository.findMinDateByCik(any())).thenReturn(DateUtils.shiftMonths(new Date(), -3));
        when(tradeRepository.findPurchaseCountByCik(any(), any(), any())).thenReturn(20, 4, 12);
        double trend = stockService.calculateTrend(new Stock());
        assertThat(trend).isEqualTo(0.44);
    }

    @Test
    public void givenStock_whenCalculateEfficiency_thenSuccess() {
        TradeRating r1 = TradeRating.builder().efficiency(0.8).weight(0.4).build();
        TradeRating r2 = TradeRating.builder().efficiency(0.3).weight(0.6).build();
        when(tradeRatingRepository.findByStockCik(any())).thenReturn(List.of(r1, r2));
        double efficiency = stockService.calculateEfficiency(new Stock());
        assertThat(efficiency).isEqualTo(0.5);
    }
}
