package com.trafficmon;

import sun.awt.geom.Crossings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LongStayOverMultipleEntryChargeSystem implements ChargePattern {

    public static final long CHARGE_SEPERATION_TIME = 1400L;
    public static final int LONGEST_MINUTE_SPENT_IN_ZONE = 240;
    public static final int LONGEST_OUT_OF_ZONE_MINUTE = 240;
    public static final BigDecimal MINIMUM_CHARGE = new BigDecimal(4);
    public static final BigDecimal MEDIUM_CHAEGE = new BigDecimal(6);
    public static final BigDecimal MAXIMUM_CHARGE = new BigDecimal(12);

    //an event log that records all boundary crossing events

    public BigDecimal specifiedChargeCalculation(List<ZoneBoundaryCrossing> crossings) {

        BigDecimal charge;

        int minutesTimeSpentInZone = 0;
        ZoneBoundaryCrossing lastEvent = crossings.get(0);

        //check if the total time spent in zone is longer than 4hours. If so, charge will be $12.
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {

            if (crossing instanceof ExitEvent) {
                minutesTimeSpentInZone += (minutesBetween(lastEvent.timestamp(), crossing.timestamp()));
            }

            lastEvent = crossing;

        }
        if (minutesTimeSpentInZone > LONGEST_MINUTE_SPENT_IN_ZONE) {
            charge = MAXIMUM_CHARGE;
        }
        //else, check if the vehicle should be charged multiple times.
        else{
            charge = chargeForThisPeriod(crossings);
        }
        return charge;
    }

    private BigDecimal chargeForThisPeriod(List<ZoneBoundaryCrossing> periodCrossings){
        ZoneBoundaryCrossing firstEntry = periodCrossings.get(0);
        long firstEntryTime = firstEntry.timestamp();
        BigDecimal periodCharge = MINIMUM_CHARGE;
        if(firstEntryTime > CHARGE_SEPERATION_TIME){
            periodCharge = MEDIUM_CHAEGE;
        }
        ZoneBoundaryCrossing lastEvent = periodCrossings.get(0);
        int currentIndex = 1;
        for(ZoneBoundaryCrossing crossing: periodCrossings.subList(1, periodCrossings.size())){
            if(crossing instanceof EntryEvent && minutesBetween(lastEvent.timestamp(), crossing.timestamp()) > LONGEST_OUT_OF_ZONE_MINUTE){
                periodCharge = periodCharge.add(chargeForThisPeriod(periodCrossings.subList(currentIndex, periodCrossings.size())));
            }
            else currentIndex++;
        }
        return periodCharge;
    }

    private int minutesBetween(long startTimeMs, long endTimeMs) {
        return (int) Math.ceil((endTimeMs - startTimeMs) / (1000.0 * 60.0));
    }

}
