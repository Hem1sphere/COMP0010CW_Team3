package com.trafficmon;

import org.jmock.Expectations;
        import org.jmock.integration.junit4.JUnitRuleMockery;
        import org.joda.time.DateTimeUtils;
        import org.junit.Rule;
        import org.junit.Test;

        import java.math.BigDecimal;

        import static com.trafficmon.RevisedChargeMethod.MAXIMUM_CHARGE;
        import static com.trafficmon.RevisedChargeMethod.MEDIUM_CHARGE;
        import static com.trafficmon.RevisedChargeMethod.MINIMUM_CHARGE;

public class SystemTest {

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private AccountsServiceProvider accountsServiceProvider = context.mock(AccountsServiceProvider.class);

    @Test
    public void SystemTest() throws AccountNotRegisteredException, InsufficientCreditException{
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(Vehicle.withRegistration("D243 5PR"), MEDIUM_CHARGE.add(MINIMUM_CHARGE));
            exactly(1).of(accountsServiceProvider).billVehicleAccount(Vehicle.withRegistration("B628 3XQ"), MINIMUM_CHARGE);
            exactly(1).of(accountsServiceProvider).billVehicleAccount(Vehicle.withRegistration("P283 2AD"), MEDIUM_CHARGE);
            exactly(1).of(accountsServiceProvider).billVehicleAccount(Vehicle.withRegistration("A123 XYZ"), MAXIMUM_CHARGE);
            exactly(1).of(accountsServiceProvider).billVehicleAccount(Vehicle.withRegistration("H374 8VX"), MEDIUM_CHARGE);
        }});

        CongestionChargeSystem congestionChargeSystem = CongestionChargeSystemBuilder.aCongestionChargeSystem().withAccountsServiceProvider(accountsServiceProvider).build();

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
    }

}