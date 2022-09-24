package com.alexeykovzel.fi.core.stock;

public class StockKeyPointFactory {

    public String[] getKeyPoints(Stock stock) {
        return new String[]{
                getActivityChange(stock),
                getRecentBigBuy(stock),
                getAvgInsiderReturn(stock)
        };
    }

    private String getActivityChange(Stock stock) {
        // e.g. lowest/highest activity in 5 years.
        return "";
    }

    private String getRecentBigBuy(Stock stock) {
        // e.g. John Smith bought shares for $5.0M 2 days ago.
        return "";
    }

    private String getAvgInsiderReturn(Stock stock) {
        // e.g. Average insider return: 25% per year.
        return "";
    }
}
