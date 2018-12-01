package com.trafficmon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OperationsTeam.class)
public class PowerLegacyTest{
        @Test
        public void doubleEntryEventTriggersInvestigation() throws Exception{
                PenaltiesService mockedOpsTeam = OperationsTeam.getInstance();
                PowerMockito.when(OperationsTeam.getInstance()).thenReturn(mockedOpsTeam);
                CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
                Vehicle testVehicle = Vehicle.withRegistration("STARTOVER");
                congestionChargeSystem.vehicleEnteringZone(testVehicle);
                congestionChargeSystem.vehicleEnteringZone(testVehicle);
                Mockito.verify(mockedOpsTeam, Mockito.times(1)).triggerInvestigationInto(testVehicle);
        }
        }
