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
import java.security.AccessControlContext;
import java.util.ArrayList;
import java.util.List;


public class LegacyProjectTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    PenaltiesService operationsTeam = context.mock(PenaltiesService.class);
    AccountsServiceProvider accountsServiceProvider = context.mock(AccountsServiceProvider.class);
    private final Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
    private final Account TEST_ACCOUNT = new Account("test", testVehicle, new BigDecimal(10));
    private final List<ZoneBoundaryCrossing> testEventLog = new ArrayList<ZoneBoundaryCrossing>();
    private ChargePattern legacyChargeSystem = new LegacyChargeSystem();



    @Test
    public void doubleEntryTriggersInvestigation() {
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void doubleExitTriggersInvestigation() {
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle); //required if not vehicle will not even be logged initially
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void deductSystemIsWorkingProperly() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, BigDecimal.valueOf(9.00).setScale(2, RoundingMode.CEILING));
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(14)
                .withSecondOfMinute(40);
        DateTime exitTime = entryTime.withHourOfDay(17)
                .withSecondOfMinute(59);
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();


        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, BigDecimal.valueOf(21.00).setScale(2, RoundingMode.CEILING));
        }});

        DateTime entryTime2 = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(18)
                .withSecondOfMinute(40);
        DateTime exitTime2 = entryTime.withHourOfDay(22)
                .withSecondOfMinute(59);
        testEventLog.add(new EntryEvent(testVehicle, entryTime2));
        testEventLog.add(new ExitEvent(testVehicle, exitTime2));
        CongestionChargeSystem congestionChargeSystem2 = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem2.calculateCharges();
    }

    @Test
    public void penaltyIsIssuedForUnregisteredAccount()  {
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).issuePenaltyNotice(testVehicle, BigDecimal.valueOf(9.00).setScale(2, RoundingMode.CEILING));
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(14);
        DateTime exitTime = entryTime.withHourOfDay(17);
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withEventLog(testEventLog).withOperationsTeam(operationsTeam).build();
        congestionChargeSystem.calculateCharges();
    }


    @Test
    public void penaltyIsIssuedForInsufficientCredit() {
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).issuePenaltyNotice(testVehicle, BigDecimal.valueOf(9.00).setScale(2, RoundingMode.CEILING));;
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(14);
        DateTime exitTime = entryTime.withHourOfDay(17);
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(new AccountsServiceProvider() {

            public void billVehicleAccount(Vehicle vehicle, BigDecimal charge) throws InsufficientCreditException {
                TEST_ACCOUNT.deduct(new BigDecimal(20));
            }
        }).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void exitTimeEarlierThanEntryTimeTriggersInvestigation(){
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(18);
        DateTime exitTime = entryTime.withHourOfDay(17);
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).withEventLog(testEventLog).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void vehicleEnteringZoneLoggedIntoChargeSystem(){
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withEventLog(testEventLog).build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        assertEquals(EntryEvent.class, testEventLog.get(0).getClass());
        assertEquals(testVehicle,testEventLog.get(0).getVehicle());
    }

    @Test
    public void vehicleLeavingZoneLoggedIntoChargeSystem(){
        //previouslyRegisteredLeaving
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withEventLog(testEventLog).build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        assertEquals(ExitEvent.class, testEventLog.get(1).getClass());
        assertEquals(testVehicle,testEventLog.get(1).getVehicle());
        //notRegisteredLeaving
        Vehicle testVehicle2 = Vehicle.withRegistration("NOTEXIST");
        congestionChargeSystem.vehicleLeavingZone(testVehicle2);
        assertEquals(2, testEventLog.size());
    }

}

