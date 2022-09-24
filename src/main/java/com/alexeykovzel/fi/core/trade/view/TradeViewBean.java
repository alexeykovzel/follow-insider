package com.alexeykovzel.fi.core.trade.view;

import com.alexeykovzel.fi.core.trade.Trade;
import com.alexeykovzel.fi.core.trade.TradeCode;
import org.springframework.stereotype.Component;

@Component
public class TradeViewBean {

    public String getType(Trade trade) {
        return TradeCode.valueOfCode(trade.getCode());
    }
}
