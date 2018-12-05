package com.trafficmon;

import java.math.BigDecimal;

public interface AccountsServiceProvider {
    Account getAccountForVehicle(Vehicle vehicle) throws AccountNotRegisteredException;
    void billAccount(Account account, BigDecimal charge) throws InsufficientCreditException;
    void billVehicleAccount(Vehicle vehicle, BigDecimal charge) throws AccountNotRegisteredException, InsufficientCreditException;
}

