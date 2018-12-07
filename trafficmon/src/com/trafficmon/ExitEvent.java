package com.trafficmon;

import org.joda.time.DateTime;

class ExitEvent extends ZoneBoundaryCrossing {
    ExitEvent(Vehicle vehicle) {
        super(vehicle);
    }

    ExitEvent(Vehicle vehicleRegistration, DateTime time) {
        super(vehicleRegistration, time);
    }
}
