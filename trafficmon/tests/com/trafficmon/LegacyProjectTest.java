package com.trafficmon;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class LegacyProjectTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    PenaltiesService operationsTeam = context.mock(PenaltiesService.class);
    AccountsService accountsService = context.mock(AccountsService.class);
    private final Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
    private final List<ZoneBoundaryCrossing> testEventLog = new ArrayList<ZoneBoundaryCrossing>();


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
            exactly(1).of(accountsService).accountFor(testVehicle);
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

