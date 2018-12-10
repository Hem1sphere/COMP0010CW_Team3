package com.trafficmon;

import java.math.BigDecimal;

public class AccountsServiceAdapter implements AccountsServiceProvider {


    private final AccountsService accountsService;

    public AccountsServiceAdapter(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    public void billVehicleAccount(Vehicle vehicle, BigDecimal charge) throws AccountNotRegisteredException, InsufficientCreditException {
        accountsService.accountFor(vehicle).deduct(charge);
    }


}
