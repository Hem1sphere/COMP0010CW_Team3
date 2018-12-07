package com.trafficmon;

import org.joda.time.DateTime;

class EntryEvent extends ZoneBoundaryCrossing {
    EntryEvent(Vehicle vehicleRegistration) {
        super(vehicleRegistration);
    }

    EntryEvent(Vehicle vehicleRegistration, DateTime time) {
        super(vehicleRegistration, time);
    }
}
