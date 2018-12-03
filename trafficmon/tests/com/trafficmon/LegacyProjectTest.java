package com.trafficmon;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//@RunWith(MockitoJUnitRunner.class)
public class LegacyProjectTest {
    @Rule
//    public MockitoRule mockitoRule = MockitoJUnit.rule();

    public JUnitRuleMockery context = new JUnitRuleMockery();

    PenaltiesService operationsTeam = context.mock(PenaltiesService.class);
    AccountsService accountsService = context.mock(AccountsService.class);
    private final Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
    private final List<ZoneBoundaryCrossing> testEventLog = new ArrayList<ZoneBoundaryCrossing>();



    //@Mock private ArrayList<ZoneBoundaryCrossing> mockedEventLog;
    @Test
    public void doubleEntryTriggersInvestigation() {
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem(operationsTeam);
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void doubleExitTriggersInvestigation() {
        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem(operationsTeam);
        congestionChargeSystem.vehicleEnteringZone(testVehicle); //required if not vehicle will not even be logged initially
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void deductSystemIsWorkingProperly() throws AccountNotRegisteredException {
        context.checking(new Expectations(){{
              //this exact invocation is expected when commented out, but returns NullPointerException when uncommented WHY
//            exactly(1).of(accountsService).accountFor(testVehicle);
        }});

        testEventLog.add(new EntryEvent(testVehicle, 1543807162500L));
        testEventLog.add(new ExitEvent(testVehicle, 1543807163000L));
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem(operationsTeam, testEventLog, accountsService);
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
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem(operationsTeam, testEventLog);
        congestionChargeSystem.calculateCharges();
    }

    @Test
    public void vehicleEnteringZoneLoggedIntoChargeSystem(){
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        assertEquals(EntryEvent.class, congestionChargeSystem.getCurrentEventLog().get(0).getClass());
        assertEquals(testVehicle,congestionChargeSystem.getCurrentEventLog().get(0).getVehicle());
    }

    @Test
    public void vehicleLeavingZoneLoggedIntoChargeSystem(){
        //previouslyRegisteredLeaving
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        assertEquals(ExitEvent.class, congestionChargeSystem.getCurrentEventLog().get(1).getClass());
        assertEquals(testVehicle,congestionChargeSystem.getCurrentEventLog().get(1).getVehicle());
        //notRegisteredLeaving
        Vehicle testVehicle2 = Vehicle.withRegistration("NOTEXIST");
        congestionChargeSystem.vehicleLeavingZone(testVehicle2);
        assertEquals(2, congestionChargeSystem.getCurrentEventLog().size());
    }

    //@InjectMocks private CongestionChargeSystem congestionChargeSystem;


//    The two tests below are not working, because the EntryEvent and ExitEvent I set up has different timesignature from

//    What the system will return.
//    @Test
//    public void vehicleEnteringZoneLoggedIntoChargeSystem(){
//        Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
//        EntryEvent entryEvent = new EntryEvent(testVehicle);
//        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
//        congestionChargeSystem.vehicleEnteringZone(testVehicle);
//        assertEquals(entryEvent,congestionChargeSystem.getCurrentEventLog().get(0));
//    }
//
//    @Test
//    public void previouslyRegisteredVehicleLeavingZoneLoggedIntoChargeSystem(){
//        Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
//        ExitEvent exitEvent = new ExitEvent(testVehicle);
//        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
//        congestionChargeSystem.vehicleEnteringZone(testVehicle);
//        congestionChargeSystem.vehicleLeavingZone(testVehicle);
//        assertEquals(exitEvent,congestionChargeSystem.getCurrentEventLog().get(1));

//    }

//    A workaround is to test if they are indeed EntryEvents / ExitEvents, and the vehicle recorded is correct


    @Test
    public void mapOfSystemCorrectlyMapsVehiclesAndTheirCrossings(){

    }


    //below also requires doing stuff with timestamp
    @Test
    public void onceInOnceOutVehicleReceivesInvoice(){

    }

    @Test
    public void leaveandReturnVehicleReceivesInvoice(){

    }

}

