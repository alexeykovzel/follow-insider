package com.alexeykovzel.fi.core.insider;

import java.util.Date;

public interface InsiderView {

    String getName();

    String getPositions();

    Date getLastActive();

    double getTotalShares();
}
