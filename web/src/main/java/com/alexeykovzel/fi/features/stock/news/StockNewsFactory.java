package com.alexeykovzel.fi.features.stock.news;

import com.alexeykovzel.fi.features.insider.Insider;
import com.alexeykovzel.fi.features.stock.Stock;
import com.alexeykovzel.fi.features.trade.Trade;
import com.alexeykovzel.fi.features.trade.TradeRepository;
import com.alexeykovzel.fi.common.DateUtils;
import com.alexeykovzel.fi.common.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StockNewsFactory {
    private final TradeRepository tradeRepository;

    public List<String> buildNews(Stock stock, int count) {
        return Stream.of(
                        exploreActivityChange(stock),
                        exploreRecentBuys(stock),
                        exploreAvgInsiderReturn(stock)
                ).filter(Objects::nonNull)
                .sorted(((n1, n2) -> n1.getHype() > n2.getHype() ? 1 : 0))
                .limit(count)
                .map(StockStory::getValue)
                .collect(Collectors.toList());
    }

    private StockStory exploreActivityChange(Stock stock) {
        // TODO: Find lowest/highest activity.
        return new StockStory("", StockStory.DEFAULT_HYPE);
    }

    @Transactional
    public StockStory exploreRecentBuys(Stock stock) {
        Date now = new Date();
        Date from = DateUtils.shiftMonths(now, -3);

        // get trade of the highest value
        Collection<Trade> trades = tradeRepository.findRecentBuysBuyCik(stock.getCik(), from);
        if (trades.isEmpty()) return null;
        Trade trade = getTradeOfHighestValue(new ArrayList<>(trades));

        // get textual values of the story
        int daysAgo = DateUtils.daysBetween(now, trade.getDate());
        List<Insider> insiders = new ArrayList<>(trade.getForm4().getInsiders());
        String othersTrail = (insiders.size() > 1) ? String.format(" and %d others", insiders.size() - 1) : "";
        String insidersValue = insiders.get(0).getName() + othersTrail;
        String tradeValue = StringUtils.formatNumber(trade.getValue());

        // TODO: Calculate story hype.
        int hype = StockStory.DEFAULT_HYPE;

        return new StockStory(String.format("%d days ago, %s purchased shares for %s",
                daysAgo, insidersValue, tradeValue), hype);
    }

    private Trade getTradeOfHighestValue(List<Trade> trades) {
        Trade trade = trades.get(0);
        for (int i = 1; i < trades.size(); i++) {
            if (trades.get(i).getValue() > trade.getValue()) {
                trade = trades.get(i);
            }
        }
        return trade;
    }

    private StockStory exploreAvgInsiderReturn(Stock stock) {
        // TODO: Find average insider return (e.g. 25% per year)
        return new StockStory("", StockStory.DEFAULT_HYPE);
    }
}
