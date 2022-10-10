package com.alexeykovzel.fi.core.stock.news;

import lombok.Data;

@Data
public class StockStory {
    public static int DEFAULT_HYPE = 1;

    private final String value;
    private final double hype;
}
