package com.trafficmon;

import java.math.BigDecimal;
import java.util.List;

import static com.trafficmon.CongestionChargeSystem.CHARGE_RATE_POUNDS_PER_MINUTE;

public class LegacyChargeSystem implements ChargePattern{
    public BigDecimal specifiedChargeCalculation(List<ZoneBoundaryCrossing> crossings) {
        BigDecimal charge = new BigDecimal(0);

        ZoneBoundaryCrossing lastEvent = crossings.get(0);


        //Adds the fees applied to  each period of stay together
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {

            if (crossing instanceof ExitEvent) {
                charge = charge.add(
                        new BigDecimal(minutesBetween(lastEvent.timestamp(), crossing.timestamp()))
                                .multiply(CHARGE_RATE_POUNDS_PER_MINUTE));
            }

            lastEvent = crossing;
        }

        return charge;
    }


    private int minutesBetween(long startTimeMs, long endTimeMs) {
        return (int) Math.ceil((endTimeMs - startTimeMs) / (1000.0 * 60.0));
    }
}
