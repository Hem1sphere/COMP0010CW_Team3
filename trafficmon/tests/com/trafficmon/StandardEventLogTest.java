package com.trafficmon;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class StandardEventLogTest {

    private final EventLog testEventLog = new StandardEventLog();
    private final Vehicle testVehicle = Vehicle.withRegistration("TEST VEHICLE");

    @Test
    public void entryIsLogged() {
        testEventLog.logEntry(testVehicle);
        assertThat(testEventLog.getNumberOfEvents(), is(1));
    }

    @Test
    public void exitIsLogged() {
        testEventLog.logExit(testVehicle);
        assertThat(testEventLog.getNumberOfEvents(), is(1));
    }

    @Test
    public void loggedVehicleIsRegistered() {
        testEventLog.logEntry(testVehicle);
        assertTrue(testEventLog.vehicleIsRegistered(testVehicle));
    }

    @Test
    public void unloggedVehicleIsNotRegistered() {
        assertFalse(testEventLog.vehicleIsRegistered(testVehicle));
    }

    @Test
    public void crossingsAreCategorisedCorrectlyByVehicles() {
        testEventLog.logEntry(testVehicle);
        testEventLog.logExit(testVehicle);
        assertEquals(testEventLog.getCrossingsByVehicle().size(), 1);
    }


}