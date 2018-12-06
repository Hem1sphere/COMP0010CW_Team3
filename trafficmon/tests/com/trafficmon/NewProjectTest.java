package com.trafficmon;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.trafficmon.CongestionChargeSystemBuilder.aCongestionChargeSystem;

public class NewProjectTest {

    public static final BigDecimal MINIMUM_CHARGE = RevisedChargeMethod.MINIMUM_CHARGE;
    public static final BigDecimal MEDIUM_CHARGE = RevisedChargeMethod.MEDIUM_CHARGE;
    public static final BigDecimal MAXIMUM_CHARGE = RevisedChargeMethod.MAXIMUM_CHARGE;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    PenaltiesService operationsTeam = context.mock(PenaltiesService.class);
    AccountsServiceProvider accountsServiceProvider = context.mock(AccountsServiceProvider.class);
    private final Vehicle testVehicle = Vehicle.withRegistration("TEST VEHICLE");
    private final Account TEST_ACCOUNT = new Account("Test Owner", testVehicle, BigDecimal.valueOf(10));
    private final List<ZoneBoundaryCrossing> testEventLog = new ArrayList<ZoneBoundaryCrossing>();
    private ChargeMethod revisedChargeMethod = new RevisedChargeMethod();


    @Test
    public void timeSpentInZoneOverTimeLimitAtOneGoIsChargedCorrectAmount() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, MAXIMUM_CHARGE);
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(6)
                .withMinuteOfHour(30);
        DateTime exitTime = entryTime.
                withHourOfDay(10)
                .withMinuteOfHour(31); //4 hour and 1 minute, should charge max
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(revisedChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void totalTimeSpentInZoneThroughoutTheDayOverTimeLimitIsChargedCorrectAmount() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, MAXIMUM_CHARGE);
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(6)
                .withMinuteOfHour(30);
        DateTime exitTime = entryTime.
                withHourOfDay(8)
                .withMinuteOfHour(30); //first period of 2hours
        DateTime entryTime2 = entryTime.
                withHourOfDay(19)
                .withMinuteOfHour(00);
        DateTime exitTime2 = entryTime.
                withHourOfDay(21)
                .withMinuteOfHour(30); //second period of 2.5hours
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        testEventLog.add(new EntryEvent(testVehicle, entryTime2));
        testEventLog.add(new ExitEvent(testVehicle, exitTime2));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(revisedChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();
    }



    @Test
    public void enterZoneBeforeSeparationTimeWithoutOverlapChargedCorrectAmount() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, MEDIUM_CHARGE);
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC) //8am to 11am
                .withHourOfDay(8);
        DateTime exitTime = entryTime.
                withHourOfDay(11);
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(revisedChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void enterZoneBeforeSeparationTimeWithOverlapChargedCorrectAmount() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, MEDIUM_CHARGE);
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC) //1pm - 3pm
                .withHourOfDay(13);
        DateTime exitTime = entryTime.
                withHourOfDay(15);
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(revisedChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void enterZoneAfterSeparationTimeChargedCorrectAmount() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, MINIMUM_CHARGE);
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC) //entry time at 3pm
                .withHourOfDay(15);
        DateTime exitTime = entryTime.
                withHourOfDay(18);
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(revisedChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void entryBeforeAndAfterSeparationTimeIsNotDoublyCharged() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, MEDIUM_CHARGE);
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(8)
                .withMinuteOfHour(30);
        DateTime exitTime = entryTime.
                withHourOfDay(10)
                .withMinuteOfHour(30); //enter before 2pm for 2 hours
        DateTime entryTime2 = entryTime.
                withHourOfDay(19)
                .withMinuteOfHour(00);
        DateTime exitTime2 = entryTime.
                withHourOfDay(20)
                .withMinuteOfHour(30); //enter after 2pm for 1.5hours, total < 4hours
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        testEventLog.add(new EntryEvent(testVehicle, entryTime2));
        testEventLog.add(new ExitEvent(testVehicle, exitTime2));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(revisedChargeMethod).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();
    }

}
