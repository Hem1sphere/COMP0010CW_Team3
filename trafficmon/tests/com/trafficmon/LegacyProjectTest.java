package com.trafficmon;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import static com.trafficmon.CongestionChargeSystemBuilder.aCongestionChargeSystem;
import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
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
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, new BigDecimal(0.05));
        }});

        testEventLog.add(new EntryEvent(testVehicle, 1543807162500L));
        testEventLog.add(new ExitEvent(testVehicle, 1543807163000L));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void penaltyIsIssuedForUnregisteredAccount()  {
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).issuePenaltyNotice(testVehicle, new BigDecimal(0.05));
        }});

        testEventLog.add(new EntryEvent(testVehicle, 1543807162500L));
        testEventLog.add(new ExitEvent(testVehicle, 1543807163000L));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withEventLog(testEventLog).withOperationsTeam(operationsTeam).build();
        congestionChargeSystem.calculateCharges();
    }


    @Test
    public void penaltyIsIssuedForInsufficientCredit() {
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).issuePenaltyNotice(testVehicle, new BigDecimal(0.05));;
        }});

        testEventLog.add(new EntryEvent(testVehicle, 1543807162500L));
        testEventLog.add(new ExitEvent(testVehicle, 1543807163000L));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(new AccountsServiceProvider() {

            public void billVehicleAccount(Vehicle vehicle, BigDecimal charge) throws InsufficientCreditException {
                TEST_ACCOUNT.deduct(new BigDecimal(20));
            }
        }).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void exitTimeEarlierThanEntryTimeTriggersInvestigation(){
        //Needs to work on Timestamp
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        testEventLog.add(new EntryEvent(testVehicle, 1543807162500L));
        testEventLog.add(new ExitEvent(testVehicle, 1543807162400L));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withOperationsTeam(operationsTeam).withEventLog(testEventLog).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void vehicleEnteringZoneLoggedIntoChargeSystem(){
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        assertEquals(EntryEvent.class, congestionChargeSystem.getCurrentEventLog().get(0).getClass());
        assertEquals(testVehicle,congestionChargeSystem.getCurrentEventLog().get(0).getVehicle());
    }

    @Test
    public void vehicleLeavingZoneLoggedIntoChargeSystem(){
        //previouslyRegisteredLeaving
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().build();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        assertEquals(ExitEvent.class, congestionChargeSystem.getCurrentEventLog().get(1).getClass());
        assertEquals(testVehicle,congestionChargeSystem.getCurrentEventLog().get(1).getVehicle());
        //notRegisteredLeaving
        Vehicle testVehicle2 = Vehicle.withRegistration("NOTEXIST");
        congestionChargeSystem.vehicleLeavingZone(testVehicle2);
        assertEquals(2, congestionChargeSystem.getCurrentEventLog().size());
    }


    @Test
    public void mapOfSystemCorrectlyMapsVehiclesAndTheirCrossings(){

    }


    //below also requires doing stuff with timestamp
    @Test
    public void onceInOnceOutVehicleReceivesInvoice(){
        Vehicle vehicle01 = Vehicle.withRegistration("M4A1 CQB");
        List<ZoneBoundaryCrossing> eventLog = new ArrayList<ZoneBoundaryCrossing>();
        eventLog.add(new EntryEvent(vehicle01, 15438071600L));
        eventLog.add(new ExitEvent(vehicle01, 15438071600L));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(legacyChargeSystem).withEventLog(eventLog).build();
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void leaveandReturnVehicleReceivesInvoice(){

    }

}

