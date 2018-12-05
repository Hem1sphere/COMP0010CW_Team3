package com.trafficmon;

import org.joda.time.DateTime;

public class ExitEvent extends ZoneBoundaryCrossing {
    public ExitEvent(Vehicle vehicle) {
        super(vehicle);
    }

    public ExitEvent(Vehicle vehicleRegistration, DateTime time) {
        super(vehicleRegistration, time);
    }
}
