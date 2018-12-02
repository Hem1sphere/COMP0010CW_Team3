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
        RegisteredCustomerAccountsService as = Mockito.mock(RegisteredCustomerAccountsService.class);
        Account acc = Mockito.mock(Account.class);

        when(RegisteredCustomerAccountsService.getInstance()).thenReturn(as);
        when(OperationsTeam.getInstance()).thenReturn(mockedOpsTeam);

        CongestionChargeSystem cgs = new CongestionChargeSystem();

        Vehicle v1 = Vehicle.withRegistration("SKS7845B");
        when(as.accountFor(any(Vehicle.class))).thenReturn(acc);
        BigDecimal charge = BigDecimal.valueOf(123);
        doThrow(new InsufficientCreditException(charge)).when(acc).deduct(charge);
        cgs.vehicleEnteringZone(v1);
        cgs.vehicleLeavingZone(v1);
        cgs.calculateCharges();
//        verify(mockedOpsTeam).issuePenaltyNotice(any(Vehicle.class), any(BigDecimal.class));

    }
}
