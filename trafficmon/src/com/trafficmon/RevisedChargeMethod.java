package com.trafficmon;

import org.joda.time.DateTime;
import java.math.BigDecimal;
import java.util.List;

public class RevisedChargeMethod implements ChargeMethod {

    public static final int CHARGE_SEPARATION_TIME = 14;
    public static final int LONGEST_MINUTE_SPENT_IN_ZONE = 240;
    public static final int LONGEST_OUT_OF_ZONE_MINUTE = 240;
    public static final BigDecimal MINIMUM_CHARGE = BigDecimal.valueOf(4);
    public static final BigDecimal MEDIUM_CHARGE = BigDecimal.valueOf(6);
    public static final BigDecimal MAXIMUM_CHARGE = BigDecimal.valueOf(12);


    public BigDecimal calculateChargeForVehicle(List<ZoneBoundaryCrossing> crossings) {

        BigDecimal charge;

        int minutesTimeSpentInZone = 0;
        ZoneBoundaryCrossing lastEvent = crossings.get(0);

        //check if the total time spent in zone is longer than 4hours. If so, charge will be $12.
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {

            if (crossing instanceof ExitEvent) {
                minutesTimeSpentInZone += (TimeManagement.minutesBetween(lastEvent.timestamp(), crossing.timestamp()));
            }

            lastEvent = crossing;

        }
        if (minutesTimeSpentInZone > LONGEST_MINUTE_SPENT_IN_ZONE) {
            charge = MAXIMUM_CHARGE;
        }
        //else, check if the vehicle should be charged multiple times.
        //If the driver should be charged an ADDITIONAL 12 pounds, simply delete else and do charge += chargeForThisPeriod(croossings);
        else{
            charge = chargeForThisPeriod(crossings);
            //charge = chargeForThisPeriodWK(crossings);
        }
        return charge;
    }

    private BigDecimal chargeForThisPeriod(List<ZoneBoundaryCrossing> periodCrossings){
        ZoneBoundaryCrossing firstEntry = periodCrossings.get(0);
        DateTime firstEntryTime = firstEntry.timestamp();
        BigDecimal periodCharge = MEDIUM_CHARGE;
        if(firstEntryTime.getHourOfDay() >= CHARGE_SEPARATION_TIME){
            periodCharge = MINIMUM_CHARGE;
        }
        ZoneBoundaryCrossing lastEvent = periodCrossings.get(0);
        int currentIndex = 1;
        for(ZoneBoundaryCrossing crossing: periodCrossings.subList(1, periodCrossings.size())){
            if(crossing instanceof EntryEvent && TimeManagement.minutesBetween(lastEvent.timestamp(), crossing.timestamp()) > LONGEST_OUT_OF_ZONE_MINUTE){
                periodCharge = periodCharge.add(chargeForThisPeriod(periodCrossings.subList(currentIndex, periodCrossings.size())));
            }
            else currentIndex++;
        }
        return periodCharge;
    }

    private BigDecimal chargeForThisPeriodWK(List<ZoneBoundaryCrossing> periodCrossings){
        ZoneBoundaryCrossing firstEntry = periodCrossings.get(0);
        DateTime firstEntryTime = firstEntry.timestamp();
        if(firstEntryTime.getHourOfDay() >= CHARGE_SEPARATION_TIME){
             return MINIMUM_CHARGE;
        }
        else {
            return MEDIUM_CHARGE;
        }
    }


}
