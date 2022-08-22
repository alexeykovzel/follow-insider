package com.alexeykovzel.fi.trade;

import com.alexeykovzel.fi.features.trade.Trade;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.features.trade.TradeService;
import com.alexeykovzel.fi.features.stock.StockRecordRepository;
import com.alexeykovzel.fi.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @Mock
    private StockRecordRepository stockRecordRepository;

    @InjectMocks
    private TradeService tradeService;

    @Test
    public void givenTransaction_whenCalculateEfficiency_thenSuccess() {
        Date currentDate = new Date();
        Trade trade = mock(Trade.class);
        when(trade.getDate()).thenReturn(currentDate);
        when(trade.getSharePrice()).thenReturn(20.0);
        when(trade.getShareCount()).thenReturn(100.0);
        when(tradeRepository.findSymbolById(any())).thenReturn("");
        when(stockRecordRepository.findNearestPrice(any())).thenReturn(25.0);
        when(stockRecordRepository.findDividendsBetween(any(), any(), any())).thenReturn(200.0);
        double roi = tradeService.calculateAnnualisedRoi(trade, DateUtils.shiftMonths(currentDate, 3), 3);
        assertThat(roi).isCloseTo(2.3215, within(0.001));
    }

    @Test
    public void givenTransaction_whenCalculateWeight_thenSuccess() {
        Trade trade = mock(Trade.class);
        when(trade.getLeftShares()).thenReturn(10000.0);
        when(trade.getShareCount()).thenReturn(1000.0);
        when(tradeRepository.findAveragePurchasedShares()).thenReturn(6400.0);
        double weight = tradeService.calculateWeight(trade);
        assertThat(weight).isCloseTo(0.0295, within(0.001));
    }
}
