package com.trafficmon;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Assert.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class LegacyProjectTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();

    @Mock Vehicle mockedVehicle;

    final EntryEvent VEHICLE_ENTRY = new EntryEvent(mockedVehicle);

    @Mock ArrayList<ZoneBoundaryCrossing> mockedEventLog;

    @Test
    public void registeredVehicleCrossingBoundaryLoggedIntoChargeSystem(){
        congestionChargeSystem.vehicleEnteringZone(mockedVehicle);
        //eventlog should add a new EntryEvent(mockedVehicle)
        Mockito.verify(mockedEventLog, Mockito.times(1)).add(VEHICLE_ENTRY);
    }

    @Test
    public void exitTimeEarlierThanEntryTimeTriggersInvestigation(){

    }

    @Test
    public void doubleEntryEventTriggersInvestigation(){

    }

    @Test
    public void doubleExitEventTriggersInvestigation(){

    }

    @Test
    public void accountNotRegisteredReceivesPenalty(){

    }

    @Test
    public void accountInsufficientBalanceReceivesPenalty(){

    }

    @Test
    public void onceInOnceOutVehicleReceivesInvoice(){

    }

    @Test
    public void leaveandReturnVehicleReceivesInvoice(){

    }

}
