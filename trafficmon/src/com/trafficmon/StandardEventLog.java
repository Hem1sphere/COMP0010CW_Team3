package com.trafficmon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardEventLog implements EventLog {

    private List<ZoneBoundaryCrossing> eventLog;

    public StandardEventLog() {
        this.eventLog = new ArrayList<ZoneBoundaryCrossing>();
    }

    public boolean orderingIsValid(List<ZoneBoundaryCrossing> crossings) {
        ZoneBoundaryCrossing lastEvent = crossings.get(0);
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {
            if (crossing.timestamp().isBefore( lastEvent.timestamp())) {
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

    public boolean vehicleIsRegistered(Vehicle vehicle) {
        for (ZoneBoundaryCrossing crossing : eventLog) {
            if (crossing.getVehicle().equals(vehicle)) {
                return true;
            }
        }
        return false;
    }

    public Map<Vehicle, List<ZoneBoundaryCrossing>> getCrossingsByVehicle() {
        Map<Vehicle, List<ZoneBoundaryCrossing>> crossingsByVehicle = new HashMap<Vehicle, List<ZoneBoundaryCrossing>>();
        for (ZoneBoundaryCrossing crossing : eventLog) {
            if (!crossingsByVehicle.containsKey(crossing.getVehicle())) {
                crossingsByVehicle.put(crossing.getVehicle(), new ArrayList<ZoneBoundaryCrossing>());
            }
            crossingsByVehicle.get(crossing.getVehicle()).add(crossing);
        }
        return crossingsByVehicle;
    }

    public void logEntry(Vehicle vehicle) {
        eventLog.add(new EntryEvent(vehicle));
    }

    public void logExit(Vehicle vehicle) {
        eventLog.add(new ExitEvent(vehicle));
    }

    public void logEntryEvent(EntryEvent entryEvent) {
        eventLog.add(entryEvent);
    }

    public void logExitEvent(ExitEvent exitEvent) {
        eventLog.add(exitEvent);
    }

    public int getNumberOfEvents() {
        return eventLog.size();
    }
}
