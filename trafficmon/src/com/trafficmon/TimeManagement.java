package com.trafficmon;

import org.joda.time.DateTime;
import org.joda.time.Duration;

public class TimeManagement {
    public static int minutesBetween(DateTime startTime, DateTime endTime) {
        Duration duration = new Duration(startTime, endTime);
        return (int) duration.getStandardMinutes();
    }
}
