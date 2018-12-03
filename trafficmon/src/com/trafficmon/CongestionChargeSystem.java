package com.trafficmon;

import java.math.BigDecimal;
import java.util.*;

public class CongestionChargeSystem {

    public static final BigDecimal CHARGE_RATE_POUNDS_PER_MINUTE = new BigDecimal(0.05);

    //an event log that records all boundary crossing events
    private final List<ZoneBoundaryCrossing> eventLog; //extract as interface????
    private final PenaltiesService operationsTeam;
    private final AccountsService accountsService;

    //multiple constructors are set up for testing purposes, could refactor to builder?
    public CongestionChargeSystem() {
        operationsTeam = OperationsTeam.getInstance();
        eventLog = new ArrayList<ZoneBoundaryCrossing>();
        accountsService = RegisteredCustomerAccountsService.getInstance();
    }

    public CongestionChargeSystem(PenaltiesService operationsTeam) {
        this.operationsTeam = operationsTeam;
        eventLog = new ArrayList<ZoneBoundaryCrossing>();
        accountsService = RegisteredCustomerAccountsService.getInstance();
    }

    public CongestionChargeSystem(PenaltiesService operationsTeam, List<ZoneBoundaryCrossing> eventLog) {
        this.operationsTeam = operationsTeam;
        this.eventLog = eventLog;
        accountsService = RegisteredCustomerAccountsService.getInstance();
    }

    public CongestionChargeSystem(PenaltiesService operationsTeam, List<ZoneBoundaryCrossing> eventLog, AccountsService accountsService) {
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

    //checks if the vehicle ever passed through the boundary
    private boolean previouslyRegistered(Vehicle vehicle) {
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
