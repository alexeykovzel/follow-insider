package com.alexeykovzel.fi.features.stock;

import com.alexeykovzel.fi.features.insider.InsiderView;
import lombok.Data;

import java.util.Date;

@Data
public class StockView {
    private String name;
    private String symbol;
    private String description;
    private String[] keyPoints;
    private InsiderView[] insiders;
    private Date lastActive;
    private double efficiency;
    private double trend;
    private double overall;
}
