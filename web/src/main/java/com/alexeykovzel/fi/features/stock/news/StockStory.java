package com.alexeykovzel.fi.features.stock.news;

import lombok.Data;

import java.util.Date;

@Data
public class StockStory {
    public static int DEFAULT_HYPE = 1;

    private final String value;
    private final double hype;
    private final Date date;
}
