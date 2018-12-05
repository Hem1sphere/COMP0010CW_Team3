package com.trafficmon;

import java.util.ArrayList;
import java.util.List;

public class CongestionChargeSystemBuilder {
    private PenaltiesService operationsTeam = OperationsTeam.getInstance();
    private List<ZoneBoundaryCrossing> eventLog = new ArrayList<ZoneBoundaryCrossing>();
    private AccountsServiceProvider accountsServiceProvider = new AccountsServiceProviderAdapter(RegisteredCustomerAccountsService.getInstance());
    private ChargePattern chargePattern;

    private CongestionChargeSystemBuilder() {};

    public static CongestionChargeSystemBuilder aCongestionChargeSystem() {
        return new CongestionChargeSystemBuilder();
    }

    public CongestionChargeSystemBuilder withChargeSystem(ChargePattern chargePattern){
        this.chargePattern = chargePattern;
        return this;
    }

    public CongestionChargeSystemBuilder withOperationsTeam(PenaltiesService operationsTeam) {
        this.operationsTeam = operationsTeam;
        return this;
    }

    public CongestionChargeSystemBuilder withEventLog(List<ZoneBoundaryCrossing> eventLog) {
        this.eventLog = eventLog;
        return this;
    }

    public CongestionChargeSystemBuilder withAccountsServiceProvider(AccountsServiceProvider accountsServiceProvider) {
        this.accountsServiceProvider = accountsServiceProvider;
        return this;
    }

    public CongestionChargeSystem build() {
        return new CongestionChargeSystem(chargePattern, operationsTeam, eventLog, accountsServiceProvider);
    }
}