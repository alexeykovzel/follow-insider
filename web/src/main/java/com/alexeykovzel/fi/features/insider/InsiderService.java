package com.alexeykovzel.fi.features.insider;

import com.alexeykovzel.fi.features.trade.TradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InsiderService {
    private final TradeRepository tradeRepository;

    public Date getLastActive(Insider insider) {
        return tradeRepository.findMaxDateByInsider(insider);
    }
}
