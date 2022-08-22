package com.alexeykovzel.fi.features.trade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

public interface TradeView {

    int getId();

    @Value("#{@viewBean.getType(target)}")
    String getType();

    @Value("#{target.form4.company.name}")
    String getCompany();

    @Value("#{target.form4.company.symbol}")
    String getSymbol();

    @Value("#{target.form4.insiders}")
    Collection<InsiderView> getInsiders();

    @Value("#{target.form4.url}")
    String getUrl();

    Double getSharePrice();

    Double getShareCount();

    Double getLeftShares();

    Date getDate();
}

interface InsiderView {

    String getCik();

    String getName();

    Collection<String> getPositions();
}

@Component
class ViewBean {

    public String getType(Trade trade) {
        return TradeType.valueOfCode(trade.getCode());
    }
}