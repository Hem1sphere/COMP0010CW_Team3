package com.trafficmon;

import org.joda.time.DateTime;

public class EntryEvent extends ZoneBoundaryCrossing {
    public EntryEvent(Vehicle vehicleRegistration) {
        super(vehicleRegistration);
    }

    public EntryEvent(Vehicle vehicleRegistration, DateTime time) {
        super(vehicleRegistration, time);
    }
}
