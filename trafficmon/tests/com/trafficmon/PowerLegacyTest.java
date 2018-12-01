package com.trafficmon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CongestionChargeSystem.class, OperationsTeam.class})
public class PowerLegacyTest{

        @Test
        public void doubleEntryEventTriggersInvestigation() throws Exception{
                PowerMockito.mockStatic(OperationsTeam.class);
                OperationsTeam mockedOpsTeam = Mockito.mock(OperationsTeam.class);
                //mockedOpsTeam is a mocked instance of OperationsTeam class
                Mockito.when(OperationsTeam.getInstance()).thenReturn(mockedOpsTeam);
                //When congestion charge system calls for a new OperationTeam, return the mocked instance to them
                CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
                Vehicle testVehicle = Vehicle.withRegistration("STARTOVER");
                congestionChargeSystem.vehicleEnteringZone(testVehicle);
                congestionChargeSystem.vehicleEnteringZone(testVehicle);
                congestionChargeSystem.calculateCharges();
//                assertFalse(congestionChargeSystem.pubCheckOrdering());
                Mockito.verify(mockedOpsTeam, Mockito.times(1)).triggerInvestigationInto(testVehicle);
        }
        }
