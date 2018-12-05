package com.trafficmon;

import java.math.BigDecimal;

public class AccountsServiceProviderAdapter implements AccountsServiceProvider {


    private final AccountsService accountsService;

    public AccountsServiceProviderAdapter(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    public Account getAccountForVehicle(Vehicle vehicle) throws AccountNotRegisteredException {
        return accountsService.accountFor(vehicle);
    }

    public void billAccount(Account account, BigDecimal charge) throws InsufficientCreditException {
        account.deduct(charge);
    }

    public void billVehicleAccount(Vehicle vehicle, BigDecimal charge) throws AccountNotRegisteredException, InsufficientCreditException {
        billAccount(getAccountForVehicle(vehicle), charge);
    }


}
