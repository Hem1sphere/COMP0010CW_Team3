package com.trafficmon;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class Event implements ZoneBoundaryCrossing{

    private final Vehicle vehicle;
    private final DateTime currentTime;
    private final String type;

    public Event(Vehicle vehicle, String type) {
        this.vehicle = vehicle;
        this.type = type;
        this.currentTime = new DateTime(DateTimeZone.UTC);
    }

    public Event(Vehicle vehicle, String type, DateTime time) {
        this.vehicle = vehicle;
        this.type = type;
        this.currentTime = time;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public DateTime timestamp() {
        return currentTime;
    }

    public String getType() {
        return type;
    }
}
