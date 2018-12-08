package com.trafficmon;

import java.util.List;
import java.util.Map;

public interface EventLog {
    boolean orderingIsValid(List<ZoneBoundaryCrossing> crossings);

    boolean vehicleIsRegistered(Vehicle vehicle);

    Map<Vehicle, List<ZoneBoundaryCrossing>> getCrossingsByVehicle();

    void logEntry(Vehicle vehicle);

    void logExit(Vehicle vehicle);

    void logEntryEvent(Event event);

    void logExitEvent(Event event);

    int getNumberOfEvents();
}
