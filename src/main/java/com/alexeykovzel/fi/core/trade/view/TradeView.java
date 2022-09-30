package com.alexeykovzel.fi.core.trade.view;

import com.alexeykovzel.fi.core.insider.Insider;
import com.alexeykovzel.fi.core.trade.Trade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.Collection;
import java.util.Date;

@Projection(name = "trade", types = {Trade.class})
public interface TradeView {

    @Value("#{target.id}")
    int getId();

    @Value("#{@tradeViewBean.getType(target)}")
    String getType();

    @Value("#{target.form4.stock.name}")
    String getCompany();

    @Value("#{target.form4.stock.symbol}")
    String getSymbol();

    @Value("#{target.form4.insiders}")
    Collection<InsiderView> getInsiders();

    @Value("#{target.form4.url}")
    String getUrl();

    @Value("#{target.sharePrice}")
    Double getSharePrice();

    @Value("#{target.shareCount}")
    Double getShareCount();

    @Value("#{target.leftShares}")
    Double getLeftShares();

    @Value("#{target.date}")
    Date getDate();

    @Projection(name = "insider", types = {Insider.class})
    interface InsiderView {

        @Value("#{target.cik}")
        String getCik();

        @Value("#{target.name}")
        String getName();

        @Value("#{target.positions}")
        Collection<String> getPositions();
    }
}