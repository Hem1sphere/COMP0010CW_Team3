package com.trafficmon;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import static com.trafficmon.CongestionChargeSystemBuilder.aCongestionChargeSystem;
import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class LegacyProjectTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private PenaltiesService operationsTeam = context.mock(PenaltiesService.class);
    private AccountsServiceProvider accountsServiceProvider = context.mock(AccountsServiceProvider.class);

    private final Vehicle testVehicle = Vehicle.withRegistration("TEST VEHICLE");
    private final Account TEST_ACCOUNT = new Account("Test Owner", testVehicle, new BigDecimal(10));
    private final EventLog testEventLog = new EventLog();
    private ChargeMethod legacyChargeMethod = new LegacyChargeMethod();


    @Test
    public void doubleEntryTriggersInvestigation() {
        context.checking(new Expectations() {{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeMethod).withOperationsTeam(operationsTeam).build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void doubleExitTriggersInvestigation() {
        context.checking(new Expectations() {{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeMethod).withOperationsTeam(operationsTeam).build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle); //required if not vehicle will not even be logged initially
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void deductSystemIsWorkingProperly() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations() {{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, BigDecimal.valueOf(9.00).setScale(2, RoundingMode.CEILING));
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(14)
                .withMinuteOfHour(30)
                .withSecondOfMinute(40);
        DateTime exitTime = entryTime.withHourOfDay(17)
                .withMinuteOfHour(30)
                .withSecondOfMinute(59);
        testEventLog.logEntryEvent(new EntryEvent(testVehicle, entryTime));
        testEventLog.logExitEvent(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();


        context.checking(new Expectations() {{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, BigDecimal.valueOf(21.00).setScale(2, RoundingMode.CEILING));
        }});

        //second entry for the same vehicle to verify charges add up correctly
        DateTime entryTime2 = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(18)
                .withMinuteOfHour(30)
                .withSecondOfMinute(40);
        DateTime exitTime2 = entryTime.withHourOfDay(22)
                .withMinuteOfHour(30)
                .withSecondOfMinute(59);
        testEventLog.logEntryEvent(new EntryEvent(testVehicle, entryTime2));
        testEventLog.logExitEvent(new ExitEvent(testVehicle, exitTime2));
        CongestionChargeSystem congestionChargeSystem2 = aCongestionChargeSystem().withChargeSystem(legacyChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem2.calculateCharges();
    }

    @Test
    public void penaltyIsIssuedForUnregisteredAccount() {
        context.checking(new Expectations() {{
            exactly(1).of(operationsTeam).issuePenaltyNotice(testVehicle, BigDecimal.valueOf(9.00).setScale(2, RoundingMode.CEILING));
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(14)
                .withMinuteOfHour(30);
        DateTime exitTime = entryTime.withHourOfDay(17)
                .withMinuteOfHour(30);
        testEventLog.logEntryEvent(new EntryEvent(testVehicle, entryTime));
        testEventLog.logExitEvent(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeMethod).withEventLog(testEventLog).withOperationsTeam(operationsTeam).build();
        congestionChargeSystem.calculateCharges();
    }


    @Test
    public void penaltyIsIssuedForInsufficientCredit() {
        context.checking(new Expectations() {{
            exactly(1).of(operationsTeam).issuePenaltyNotice(testVehicle, BigDecimal.valueOf(9.00).setScale(2, RoundingMode.CEILING));
            ;
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(14)
                .withMinuteOfHour(30);
        DateTime exitTime = entryTime.withHourOfDay(17)
                .withMinuteOfHour(30);
        testEventLog.logEntryEvent(new EntryEvent(testVehicle, entryTime));
        testEventLog.logExitEvent(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(new AccountsServiceProvider() {

            public void billVehicleAccount(Vehicle vehicle, BigDecimal charge) throws InsufficientCreditException {
                TEST_ACCOUNT.deduct(new BigDecimal(11)); //charge has to be above balance of TEST_ACCOUNT for exception to be thrown
            }

        }).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void exitTimeEarlierThanEntryTimeTriggersInvestigation() {
        context.checking(new Expectations() {{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(18);
        DateTime exitTime = entryTime.withHourOfDay(17);
        testEventLog.logEntryEvent(new EntryEvent(testVehicle, entryTime));
        testEventLog.logExitEvent(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void vehicleEnteringZoneIsLogged() {
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withEventLog(testEventLog).build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        assertEquals(1, testEventLog.getNumberOfEvents());
    }

    @Test
    public void vehicleLeavingZoneIsLogged() {
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withEventLog(testEventLog).build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        assertEquals(2, testEventLog.getNumberOfEvents());
    }

    @Test
    public void unregisteredVehiclesAreNotLogged() { //unregistered vehicle (i.e. exit before entry) should not be logged
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withEventLog(testEventLog).build();
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        assertEquals(0, testEventLog.getNumberOfEvents());
    }

}
