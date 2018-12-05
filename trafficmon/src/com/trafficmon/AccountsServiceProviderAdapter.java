package com.trafficmon;

import java.math.BigDecimal;

public class AccountsServiceProviderAdapter implements AccountsServiceProvider {


    private final AccountsService accountsService;

    public AccountsServiceProviderAdapter(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    public void billAccount(Vehicle vehicle, BigDecimal charge) throws AccountNotRegisteredException, InsufficientCreditException {
        accountsService.accountFor(vehicle).deduct(charge);
    }
}
