package com.alexeykovzel.fi.features.trade.view;

import com.alexeykovzel.fi.features.trade.Trade;
import com.alexeykovzel.fi.features.trade.TradeCode;
import org.springframework.stereotype.Component;

@Component
public class TradeViewBean {

    public String getType(Trade trade) {
        return trade.getCode().type;
    }
}
