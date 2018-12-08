package com.trafficmon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardEventLog implements EventLog {

    private List<ZoneBoundaryCrossing> eventLog;

    StandardEventLog() {
        this.eventLog = new ArrayList<ZoneBoundaryCrossing>();
    }

    public boolean orderingIsValid(List<ZoneBoundaryCrossing> crossings) {
        ZoneBoundaryCrossing lastEvent = crossings.get(0);
        for (ZoneBoundaryCrossing crossing : crossings.subList(1, crossings.size())) {
            if (crossing.timestamp().isBefore( lastEvent.timestamp())) {
                return false;
            }
            if (crossing.getType() == "entry" && lastEvent.getType() == "entry") {
                return false;
            }
            if (crossing.getType() == "exit" && lastEvent.getType() == "exit") {
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
        eventLog.add(new Event(vehicle, "entry"));
    }

    public void logExit(Vehicle vehicle) {
        eventLog.add(new Event(vehicle, "exit"));
    }

    public void logEvent(Event event) {
        eventLog.add(event);
    }

    public int getNumberOfEvents() {
        return eventLog.size();
    }
}
