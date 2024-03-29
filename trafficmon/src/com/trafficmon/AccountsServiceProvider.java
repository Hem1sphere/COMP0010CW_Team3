package com.trafficmon;

import java.math.BigDecimal;

public interface AccountsServiceProvider {
    void billVehicleAccount(Vehicle vehicle, BigDecimal charge) throws AccountNotRegisteredException, InsufficientCreditException;
}

