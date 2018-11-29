package com.trafficmon;

import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;

@RunWith(MockitoJUnitRunner.class)
public class LegacyProjectTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    //@Mock private ArrayList<ZoneBoundaryCrossing> mockedEventLog;

    //@InjectMocks private CongestionChargeSystem congestionChargeSystem;


    @Test
    public void registeredVehicleCrossingBoundaryLoggedIntoChargeSystem(){
        Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        assertEquals(testVehicle,congestionChargeSystem.getCurrentEventLog().get(0).getVehicle());
    }

    @Test
    public void exitTimeEarlierThanEntryTimeTriggersInvestigation(){
        //Needs to work on Timestamp
    }


    //For the next two tests, how do I know if
    @Test
    public void doubleEntryEventTriggersInvestigation(){
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        Vehicle testVehicle = Vehicle.withRegistration("STARTOVER");
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        assertFalse(congestionChargeSystem.pubCheckOrdering());
    }

    @Test
    public void doubleExitEventTriggersInvestigation(){
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        Vehicle testVehicle = Vehicle.withRegistration("STARTOVER");
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        assertFalse(congestionChargeSystem.pubCheckOrdering());
    }


    //below also requires doing stuff with timestamp
    @Test
    public void onceInOnceOutVehicleReceivesInvoice(){

    }

    @Test
    public void leaveandReturnVehicleReceivesInvoice(){

    }

}
