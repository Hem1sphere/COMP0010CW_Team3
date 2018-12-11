package com.trafficmon;

import org.joda.time.DateTimeUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SystemTest {

    @Test
    public void SystemTest() throws AccountNotRegisteredException, InsufficientCreditException{

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        CongestionChargeSystem congestionChargeSystem = CongestionChargeSystemBuilder.aCongestionChargeSystem().build();

        //05:34:03, "A123 XYZ" Entering
        DateTimeUtils.setCurrentMillisFixed(1544247243000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"));

        //08:31:18, "D243 5PR" Entering
        DateTimeUtils.setCurrentMillisFixed(1544257878000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("D243 5PR"));

        //08:49:18, "H374 8VX" Entering
        DateTimeUtils.setCurrentMillisFixed(1544258958000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("H374 8VX"));

        //09:42:13, "D243 5PR" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544262133000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("D243 5PR"));

        //09:59:59, "H374 8VX" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544263199000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("H374 8VX"));

        //13:59:59, "P283 2AD" Entering
        DateTimeUtils.setCurrentMillisFixed(1544277599000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("P283 2AD"));

        //14:00:00, "B628 3XQ" Entering
        DateTimeUtils.setCurrentMillisFixed(1544277600000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("B628 3XQ"));

        //14:14:14, "P283 2AD" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544278454000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("P283 2AD"));

        //14:27:27, "B628 3XQ" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544279267000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("B628 3XQ"));

        //14:59:08, "A123 XYZ" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544281148000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"));

        //15:31:32, "D243 5PR" Entering
        DateTimeUtils.setCurrentMillisFixed(1544283092000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("D243 5PR"));

        //15:31:33, "D243 5PR" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544283093000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("D243 5PR"));


        congestionChargeSystem.calculateCharges();

        //ensures the system works end-to-end, including third party services, without any exceptions
        assertTrue(outContent.toString().contains("Charge made to account of"));
        assertTrue(outContent.toString().contains("deducted, balance:"));
    }

}
