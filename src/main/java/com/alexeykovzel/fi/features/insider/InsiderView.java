package com.alexeykovzel.fi.features.insider;

import lombok.Data;

import java.util.Date;

@Data
public class InsiderView {
    private String name;
    private String[] positions;
    private double sharesTotal;
    private Date lastActive;
}
