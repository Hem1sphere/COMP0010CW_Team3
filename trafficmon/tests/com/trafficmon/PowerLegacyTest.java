package com.trafficmon;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;

import static com.trafficmon.CongestionChargeSystemBuilder.aCongestionChargeSystem;
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
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().build();
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
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().build();
        Vehicle testVehicle = Vehicle.withRegistration("STARTOVER");
        congestionChargeSystem.vehicleEnteringZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.vehicleLeavingZone(testVehicle);
        congestionChargeSystem.calculateCharges();
        verify(mockedOpsTeam, Mockito.times(1)).triggerInvestigationInto(testVehicle);
    }

    @Test
    public void notRegisteredOrInsufficientCreditReceivesPenalty() throws Exception {
        //static classes requiring power mock
        mockStatic(RegisteredCustomerAccountsService.class);
        mockStatic(OperationsTeam.class);

        OperationsTeam mockedOpsTeam = Mockito.mock(OperationsTeam.class);
        RegisteredCustomerAccountsService registeredCustomerAccountsService = Mockito.mock(RegisteredCustomerAccountsService.class);
        Account mockAccount1 = Mockito.mock(Account.class);

        when(RegisteredCustomerAccountsService.getInstance()).thenReturn(registeredCustomerAccountsService);
        when(OperationsTeam.getInstance()).thenReturn(mockedOpsTeam);

        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().build();

        Vehicle v1 = Vehicle.withRegistration("SCG1228G");
        when(registeredCustomerAccountsService.accountFor(v1)).thenReturn(mockAccount1);

        doThrow(InsufficientCreditException.class).when(mockAccount1).deduct(any(BigDecimal.class));

        congestionChargeSystem.vehicleEnteringZone(v1);
        congestionChargeSystem.vehicleLeavingZone(v1);
        congestionChargeSystem.calculateCharges();

//        verify(registeredCustomerAccountsService).accountFor(v1);
        verify(mockedOpsTeam).issuePenaltyNotice(eq(v1), any(BigDecimal.class));


        //NotRegistered
        Vehicle v2 = Vehicle.withRegistration("NTT1104K");
        doThrow(AccountNotRegisteredException.class).when(registeredCustomerAccountsService).accountFor(v2);

        congestionChargeSystem.vehicleEnteringZone(v2);
        congestionChargeSystem.vehicleLeavingZone(v2);
        congestionChargeSystem.calculateCharges();

        verify(mockedOpsTeam).issuePenaltyNotice(eq(v2), any(BigDecimal.class));

    }

    @Test
    public void onceInOnceOutVehicleReceivesInvoice() throws Exception{
        mockStatic(RegisteredCustomerAccountsService.class);
        Account mockedAccount = mock(Account.class);
        AccountsService registeredCustomerAccountsService = mock(RegisteredCustomerAccountsService.class);
        when(RegisteredCustomerAccountsService.getInstance()).thenReturn(registeredCustomerAccountsService);
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().build();
        Vehicle vehicle01 = Vehicle.withRegistration("VEHICLE01");
        when(registeredCustomerAccountsService.accountFor(vehicle01)).thenReturn(mockedAccount);
        congestionChargeSystem.getCurrentEventLog().add(new EntryEvent(vehicle01));
        congestionChargeSystem.getCurrentEventLog().add(new ExitEvent(vehicle01));
        congestionChargeSystem.calculateCharges();

        verify(registeredCustomerAccountsService).accountFor(vehicle01);
    }

}
