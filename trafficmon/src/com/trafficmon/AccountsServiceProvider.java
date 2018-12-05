package com.trafficmon;

import java.math.BigDecimal;

public interface AccountsServiceProvider {

    public void billAccount(Vehicle vehicle, BigDecimal charge) throws AccountNotRegisteredException, InsufficientCreditException;
}

