package com.trafficmon;

import org.joda.time.DateTimeUtils;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SystemTest {

    @Test
    public void SystemTesting() throws AccountNotRegisteredException, InsufficientCreditException{

        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        AccountsServiceProvider accountsServiceProvider = new AccountsServiceAdapter(new FakeRegisteredAccounts());

        CongestionChargeSystem congestionChargeSystem = CongestionChargeSystemBuilder.aCongestionChargeSystem().withAccountsServiceProvider(accountsServiceProvider).build();

        //05:34:03, "K769 387" Entering
        DateTimeUtils.setCurrentMillisFixed(1544247243000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("K769 387"));

        //08:31:18, "NZ87 6KX" Entering
        DateTimeUtils.setCurrentMillisFixed(1544257878000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("NZ87 6KX"));

        //08:49:18, "HE14 1MC" Entering
        DateTimeUtils.setCurrentMillisFixed(1544258958000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("HE14 1MC"));

        //09:42:13, "NZ87 6KX" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544262133000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("NZ87 6KX"));

        //09:59:59, "HE14 1MC" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544263199000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("HE14 1MC"));

        //13:59:59, "KK87 X65" Entering
        DateTimeUtils.setCurrentMillisFixed(1544277599000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("KK87 X65"));

        //14:00:00, "CT74 ACD" Entering
        DateTimeUtils.setCurrentMillisFixed(1544277600000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("CT74 ACD"));

        //14:14:14, "KK87 X65" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544278454000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("KK87 X65"));

        //14:27:27, "CT74 ACD" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544279267000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("CT74 ACD"));

        //14:59:08, "K769 387" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544281148000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("K769 387"));

        //15:31:32, "NZ87 6KX" Entering
        DateTimeUtils.setCurrentMillisFixed(1544283092000L);
        congestionChargeSystem.vehicleEnteringZone(Vehicle.withRegistration("NZ87 6KX"));

        //15:31:33, "NZ87 6KX" Leaving
        DateTimeUtils.setCurrentMillisFixed(1544283093000L);
        congestionChargeSystem.vehicleLeavingZone(Vehicle.withRegistration("NZ87 6KX"));


        congestionChargeSystem.calculateCharges();

        //ensures the system works end-to-end, including third party services, without any exceptions
        assertTrue(outContent.toString().contains("Charge made to account of"));
        assertTrue(outContent.toString().contains("deducted, balance:"));
    }

}

class FakeRegisteredAccounts implements AccountsService{
    public FakeRegisteredAccounts(){

    }

    public Account accountFor(Vehicle vehicle) throws AccountNotRegisteredException {
        Iterator i$ = this.accounts.iterator();

        Account account;
        do {
            if (!i$.hasNext()) {
                throw new AccountNotRegisteredException(vehicle);
            }

            account = (Account)i$.next();
        } while(!account.getAssociatedVehicle().equals(vehicle));

        return account;
    }

    private List<Account> accounts = new ArrayList<Account>() {
        {
            this.add(new Account("Weikang Tan", Vehicle.withRegistration("K769 387"), BigDecimal.valueOf(50)));
            this.add(new Account("Jeff Ho", Vehicle.withRegistration("NZ87 6KX"), BigDecimal.valueOf(10)));
            this.add(new Account("Haixiang Sun", Vehicle.withRegistration("HE14 1MC"), BigDecimal.valueOf(1)));
            this.add(new Account("Yuxuan Fan", Vehicle.withRegistration("KK87 X65"), BigDecimal.valueOf(5120)));
        }
    };
}

