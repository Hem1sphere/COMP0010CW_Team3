package com.trafficmon;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.print.attribute.standard.DateTimeAtCompleted;

public interface ZoneBoundaryCrossing {
    Vehicle getVehicle();
    DateTime timestamp();
}
