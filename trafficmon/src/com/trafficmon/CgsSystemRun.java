package com.trafficmon;

import org.joda.time.DateTimeUtils;

public class CgsSystemRun {

    public static void main(String[] args) {
        ChargeMethod chargeMethod = new RevisedChargeMethod();
        CongestionChargeSystem congestionChargeSystem = CongestionChargeSystemBuilder.aCongestionChargeSystem().withChargeSystem(chargeMethod).build();

        DateTimeUtils.setCurrentMillisFixed(1544247243000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"));

        DateTimeUtils.setCurrentMillisFixed(1544281148000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"));
        System.out.println("STARTING TO CALCULATE CHARGES");
        congestionChargeSystem.calculateCharges();
    }

}