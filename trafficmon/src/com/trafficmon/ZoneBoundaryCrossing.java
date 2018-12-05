package com.trafficmon;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.print.attribute.standard.DateTimeAtCompleted;

public abstract class ZoneBoundaryCrossing {

    private final Vehicle vehicle;
    private final DateTime currentTime;

    public ZoneBoundaryCrossing(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.currentTime = new DateTime(DateTimeZone.UTC);
    }

    public ZoneBoundaryCrossing(Vehicle vehicle, DateTime time) {
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
