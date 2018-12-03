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
import java.util.Arrays;

//@RunWith(MockitoJUnitRunner.class)
public class LegacyProjectTest {

    @Rule
//    public MockitoRule mockitoRule = MockitoJUnit.rule();

    public JUnitRuleMockery context = new JUnitRuleMockery();
    PenaltiesService operationsTeam = context.mock(PenaltiesService.class);

    //@Mock private ArrayList<ZoneBoundaryCrossing> mockedEventLog;

    @Test
    public void doubleEntryTriggersShit() {
        final Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");

        context.checking(new Expectations(){{
            exactly(1).of(operationsTeam).triggerInvestigationInto(testVehicle);
        }});

        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem(operationsTeam);
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.calculateCharges();
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
    public void vehicleEnteringZoneLoggedIntoChargeSystem(){
        Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        assertEquals(EntryEvent.class, congestionChargeSystem.getCurrentEventLog().get(0).getClass());
        assertEquals(testVehicle,congestionChargeSystem.getCurrentEventLog().get(0).getVehicle());
    }

    @Test
    public void vehicleLeavingZoneLoggedIntoChargeSystem(){
        //previouslyRegisteredLeaving
        Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
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

    @Test
    public void mapOfSystemCorrectlyMapsVehiclesAndTheirCrossings(){

    }


    @Test
    public void exitTimeEarlierThanEntryTimeTriggersInvestigation(){
        //Needs to work on Timestamp
    }


    //below also requires doing stuff with timestamp
    @Test
    public void onceInOnceOutVehicleReceivesInvoice(){

    }

    @Test
    public void leaveandReturnVehicleReceivesInvoice(){

    }

}

