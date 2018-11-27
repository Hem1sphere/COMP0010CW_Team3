package com.trafficmon;

import org.mockito.Mockito;
import org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.Assert.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;


public class LegacyProjectTest {

    @Mock

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();


    @Test
    public void notRegisteredVehicleCrossingBoundaryRegisteredandLogged(){

        List<ZoneBoundaryCrossing> eventlog = new ArrayList<ZoneBoundaryCrossing>();

    }

    @Test
    public void registeredVehicleCrossingBoundaryLoggedIntoChargeSystem(){

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
