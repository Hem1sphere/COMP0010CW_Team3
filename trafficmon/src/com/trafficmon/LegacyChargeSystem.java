package com.trafficmon;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.math.BigDecimal;
import java.util.List;

import static com.trafficmon.CongestionChargeSystem.CHARGE_RATE_POUNDS_PER_MINUTE;

public class LegacyChargeSystem implements ChargePattern{
    public BigDecimal specifiedChargeCalculation(List<ZoneBoundaryCrossing> crossings) {
        BigDecimal charge = BigDecimal.valueOf(0);

        ZoneBoundaryCrossing lastEvent = crossings.get(0);

        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {
            if (crossing instanceof ExitEvent) {
                charge = charge.add(
                        BigDecimal.valueOf(TimeManagement.minutesBetween(lastEvent.timestamp(), crossing.timestamp()))
                                .multiply(CHARGE_RATE_POUNDS_PER_MINUTE));
            }

            lastEvent = crossing;
        }
        return charge;
    }

}
