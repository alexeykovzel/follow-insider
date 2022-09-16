package com.alexeykovzel.fi.features;

import java.util.List;

public class KeyPointFactory {

    public List<String> getKeyPoints(String stock) {
        return List.of(
                getActivityChange(stock),
                getRecentBigBuy(stock),
                getAvgInsiderReturn(stock)
        );
    }

    private String getActivityChange(String stock) {
        // e.g. lowest/highest activity in 5 years.
        return "";
    }

    private String getRecentBigBuy(String stock) {
        // e.g. John Smith bought shares for $5.0M 2 days ago.
        return "";
    }

    private String getAvgInsiderReturn(String stock) {
        // e.g. Average insider return: 25% per year.
        return "";
    }
}
