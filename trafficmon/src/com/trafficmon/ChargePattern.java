package com.trafficmon;

import java.math.BigDecimal;
import java.util.List;

public interface ChargePattern {
    public BigDecimal calculateChargeForVehicle(List<ZoneBoundaryCrossing> crossings);
}
