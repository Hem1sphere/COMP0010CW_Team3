package com.trafficmon;

public class ExitEvent extends ZoneBoundaryCrossing {
    public ExitEvent(Vehicle vehicle) {
        super(vehicle);
    }

    public ExitEvent(Vehicle vehicleRegistration, long time) {
        super(vehicleRegistration, time);
    }
}
