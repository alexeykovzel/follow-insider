package com.alexeykovzel.fi.features.trade.view;

import com.alexeykovzel.fi.features.trade.Trade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "tradePoint", types = {Trade.class})
public interface TradePoint {

    @Value("#{target.shareCount}")
    double getShareCount();

    @Value("#{target.date}")
    Date getDate();
}
