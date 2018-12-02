package com.trafficmon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CongestionChargeSystem.class, OperationsTeam.class, RegisteredCustomerAccountsService.class})
public class PowerLegacyTest{

    @Test
    public void doubleEntryEventTriggersInvestigation() throws Exception{
        mockStatic(OperationsTeam.class);
        OperationsTeam mockedOpsTeam = Mockito.mock(OperationsTeam.class);
        //mockedOpsTeam is a mocked instance of OperationsTeam class
        when(OperationsTeam.getInstance()).thenReturn(mockedOpsTeam);
        //When congestion charge system calls for a new OperationTeam, return the mocked instance to them
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        Vehicle testVehicle = Vehicle.withRegistration("STARTOVER");
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.calculateCharges();
        verify(mockedOpsTeam, Mockito.times(1)).triggerInvestigationInto(testVehicle);
    }

    @Test
    public void doubleExitEventTriggersInvestigation() throws Exception{
        mockStatic(OperationsTeam.class);
        OperationsTeam mockedOpsTeam = Mockito.mock(OperationsTeam.class);
        when(OperationsTeam.getInstance()).thenReturn(mockedOpsTeam);
        CongestionChargeSystem congestionChargeSystem = new CongestionChargeSystem();
        Vehicle testVehicle = Vehicle.withRegistration("STARTOVER");
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.calculateCharges();
        verify(mockedOpsTeam, Mockito.times(1)).triggerInvestigationInto(testVehicle);
    }

    @Test
    public void driversChargedPenaltyIfInsufficientCredit() throws Exception {
        //static classes requiring power mock
        mockStatic(RegisteredCustomerAccountsService.class);
        mockStatic(OperationsTeam.class);

        OperationsTeam mockedOpsTeam = Mockito.mock(OperationsTeam.class);
        RegisteredCustomerAccountsService mockRegCustAccService = Mockito.mock(RegisteredCustomerAccountsService.class);
        Account mockAccount = Mockito.mock(Account.class);

        when(RegisteredCustomerAccountsService.getInstance()).thenReturn(mockRegCustAccService);
        when(OperationsTeam.getInstance()).thenReturn(mockedOpsTeam);

        CongestionChargeSystem cgs = new CongestionChargeSystem();

        Vehicle v1 = Vehicle.withRegistration("SCG1228G");
        when(mockRegCustAccService.accountFor(any(Vehicle.class))).thenReturn(mockAccount);

        doThrow(InsufficientCreditException.class).when(mockAccount).deduct(any(BigDecimal.class));

        cgs.vehicleEnteringZone(v1);
        cgs.vehicleLeavingZone(v1);
        cgs.calculateCharges();

        verify(mockedOpsTeam).issuePenaltyNotice(eq(v1), any(BigDecimal.class));

    }
}
