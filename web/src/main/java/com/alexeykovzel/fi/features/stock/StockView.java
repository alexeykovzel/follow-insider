package com.alexeykovzel.fi.features.stock;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StockView {
    private String name;
    private String symbol;
    private String description;
    private List<String> keyPoints;
    private Date lastActive;
    private double efficiency;
    private double trend;
    private double overall;
}
