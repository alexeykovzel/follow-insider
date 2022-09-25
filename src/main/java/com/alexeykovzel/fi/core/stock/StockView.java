package com.alexeykovzel.fi.core.stock;

import lombok.Data;

import java.util.Collection;
import java.util.Date;

@Data
public class StockView {
    private String name;
    private String symbol;
    private String description;
    private String[] keyPoints;
    private Date lastActive;
    private double efficiency;
    private double trend;
    private double overall;
}
