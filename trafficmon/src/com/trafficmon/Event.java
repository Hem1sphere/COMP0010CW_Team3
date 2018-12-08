package com.trafficmon;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Event implements ZoneBoundaryCrossing{

    private final Vehicle vehicle;
    private final DateTime currentTime;

    public Event(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.currentTime = new DateTime(DateTimeZone.UTC);
    }

    public Event(Vehicle vehicle, DateTime time) {
        this.vehicle = vehicle;
        this.currentTime = time;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public DateTime timestamp() {
        return currentTime;
    }
}
