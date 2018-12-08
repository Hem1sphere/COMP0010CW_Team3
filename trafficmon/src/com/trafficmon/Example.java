package com.trafficmon;

import static com.trafficmon.CongestionChargeSystemBuilder.aCongestionChargeSystem;

public class Example {
    //quite useful to run the code and use debugger to see variable values
    public static void main(String[] args) throws Exception {
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().build();
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("A123 XYZ"));
//        delaySeconds(10);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"));
//        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("J091 4PY"));
//        delayMinutes(30);
//        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("A123 XYZ"));
//        delayMinutes(10);
//        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("J091 4PY"));
        congestionChargeSystem.calculateCharges();
    }
    private static void delayMinutes(int mins) throws InterruptedException {
        delaySeconds(mins * 60);
    }
    private static void delaySeconds(int secs) throws InterruptedException {
        Thread.sleep(secs * 1000);
    }
}
