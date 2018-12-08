package com.trafficmon;

import org.joda.time.DateTime;

class ExitEvent extends Event {
    ExitEvent(Vehicle vehicleRegistration) {
        super(vehicleRegistration);
    }

    ExitEvent(Vehicle vehicleRegistration, DateTime time) {
        super(vehicleRegistration, time);
    }
}
