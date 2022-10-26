package com.alexeykovzel.fi.features.insider;

import java.util.Collection;
import java.util.Date;

public interface InsiderView {

    String getName();

    Collection<String> getPositions();

    Date getLastActive();

    double getTotalShares();
}
