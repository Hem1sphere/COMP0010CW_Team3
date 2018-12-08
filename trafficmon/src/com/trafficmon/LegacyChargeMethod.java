package com.trafficmon;

import java.math.BigDecimal;
import java.util.List;

import static com.trafficmon.CongestionChargeSystem.CHARGE_RATE_POUNDS_PER_MINUTE;


public class LegacyChargeMethod implements ChargeMethod {
    public BigDecimal calculateChargeForVehicle(List<ZoneBoundaryCrossing> crossings) {
        BigDecimal charge = BigDecimal.valueOf(0);

        ZoneBoundaryCrossing lastEvent = crossings.get(0);

        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {
            if (crossing.getType() == "exit") {
                charge = charge.add(
                        BigDecimal.valueOf(TimeManagement.minutesBetween(lastEvent.timestamp(), crossing.timestamp()))
                                .multiply(CHARGE_RATE_POUNDS_PER_MINUTE));
            }

            lastEvent = crossing;
        }
        return charge;
    }

}
