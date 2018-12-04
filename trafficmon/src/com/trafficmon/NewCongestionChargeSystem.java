package com.trafficmon;

import sun.awt.geom.Crossings;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewCongestionChargeSystem {

    public static final long CHARGE_SEPERATION_TIME = 1400L;
    public static final int LONGEST_MINUTE_SPENT_IN_ZONE = 240;
    public static final int LONGEST_OUT_OF_ZONE_MINUTE = 240;
    public static final BigDecimal MINIMUM_CHARGE = new BigDecimal(4);
    public static final BigDecimal MEDIUM_CHAEGE = new BigDecimal(6);
    public static final BigDecimal MAXIMUM_CHARGE = new BigDecimal(12);

    //an event log that records all boundary crossing events
    private final List<ZoneBoundaryCrossing> eventLog; //extract as interface????
    private final PenaltiesService operationsTeam;
    private final AccountsService accountsService;


    public NewCongestionChargeSystem(PenaltiesService operationsTeam, List<ZoneBoundaryCrossing> eventLog, AccountsService accountsService) {
        this.operationsTeam = operationsTeam;
        this.eventLog = eventLog;
        this.accountsService = accountsService;
    }


    public void vehicleEnteringZone(Vehicle vehicle) {
        eventLog.add(new EntryEvent(vehicle));
    }

    public void vehicleLeavingZone(Vehicle vehicle) {
        if (!previouslyRegistered(vehicle)) {
            return;
        }
        eventLog.add(new ExitEvent(vehicle));
    }

    //Calculates charges at the end of the day
    public void calculateCharges() {

        //A Map to map crossing events to each vehicle
        Map<Vehicle, List<ZoneBoundaryCrossing>> crossingsByVehicle = new HashMap<Vehicle, List<ZoneBoundaryCrossing>>();

        //iterate through boundary crossing events
        for (ZoneBoundaryCrossing crossing : eventLog) {

            //check if a vehicle has not already been added to the map
            if (!crossingsByVehicle.containsKey(crossing.getVehicle())) {
                //if not added then create its entry with empty boundary crossing event list
                crossingsByVehicle.put(crossing.getVehicle(), new ArrayList<ZoneBoundaryCrossing>());
            }
            //add this boundary crossing event to this vehicle
            crossingsByVehicle.get(crossing.getVehicle()).add(crossing);
        }


        //Entry that includes a vehicle and all its boundary crossing events (lists)
        for (Map.Entry<Vehicle, List<ZoneBoundaryCrossing>> vehicleCrossings : crossingsByVehicle.entrySet()) {
            Vehicle vehicle = vehicleCrossings.getKey();
            List<ZoneBoundaryCrossing> crossings = vehicleCrossings.getValue();

            if (!checkOrderingOf(crossings)) {
                operationsTeam.triggerInvestigationInto(vehicle);
            } else {

                BigDecimal charge = calculateChargeForTimeInZone(crossings);

                try {
                    //should use an adapter but existing class is already based off an interface
                    accountsService.accountFor(vehicle).deduct(charge);
                } catch (InsufficientCreditException ice) {
                    operationsTeam.issuePenaltyNotice(vehicle, charge);
                } catch (AccountNotRegisteredException e) {
                    operationsTeam.issuePenaltyNotice(vehicle, charge);
                }
            }
        }
    }

    private BigDecimal calculateChargeForTimeInZone(List<ZoneBoundaryCrossing> crossings) {

        BigDecimal charge = MINIMUM_CHARGE;

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

        }


        return charge;
    }

    private BigDecimal chargeForThisPeriod(List<ZoneBoundaryCrossing> periodCrossings){
        ZoneBoundaryCrossing firstEntry = periodCrossings.get(0);
        long firstEntryTime = firstEntry.timestamp();
        BigDecimal periodCharge = new BigDecimal(0);
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

    //checks if the vehicle ever passed through the boundary
    public boolean previouslyRegistered(Vehicle vehicle) {
        for (ZoneBoundaryCrossing crossing : eventLog) {
            if (crossing.getVehicle().equals(vehicle)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkOrderingOf(List<ZoneBoundaryCrossing> crossings) {

        //lastevent for the earlier event, croosing for the later event, compare the two and iterate through the list
        ZoneBoundaryCrossing lastEvent = crossings.get(0);

        //sublist excludes checked event
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {
            //Below are abnormal events that triggers investigation
            if (crossing.timestamp() < lastEvent.timestamp()) {
                return false;
            }
            if (crossing instanceof EntryEvent && lastEvent instanceof EntryEvent) {
                return false;
            }
            if (crossing instanceof ExitEvent && lastEvent instanceof ExitEvent) {
                return false;
            }
            lastEvent = crossing;
        }

        return true;
    }

    private int minutesBetween(long startTimeMs, long endTimeMs) {
        return (int) Math.ceil((endTimeMs - startTimeMs) / (1000.0 * 60.0));
    }



    //Below are getter methods for testing
    public List<ZoneBoundaryCrossing> getCurrentEventLog(){
        return eventLog;
    }

}
