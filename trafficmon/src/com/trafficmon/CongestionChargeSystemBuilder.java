package com.trafficmon;

class CongestionChargeSystemBuilder {
    private PenaltiesService operationsTeam = OperationsTeam.getInstance();
    private EventLog eventLog = new StandardEventLog();
    private AccountsServiceProvider accountsServiceProvider = new AccountsServiceProviderAdapter(RegisteredCustomerAccountsService.getInstance());
    private ChargeMethod chargeMethod;

    private CongestionChargeSystemBuilder() {};

    public static CongestionChargeSystemBuilder aCongestionChargeSystem() {
        return new CongestionChargeSystemBuilder();
    }

    CongestionChargeSystemBuilder withChargeSystem(ChargeMethod chargeMethod){
        this.chargeMethod = chargeMethod;
        return this;
    }

    CongestionChargeSystemBuilder withOperationsTeam(PenaltiesService operationsTeam) {
        this.operationsTeam = operationsTeam;
        return this;
    }

    CongestionChargeSystemBuilder withEventLog(EventLog eventLog) {
        this.eventLog = eventLog;
        return this;
    }

    CongestionChargeSystemBuilder withAccountsServiceProvider(AccountsServiceProvider accountsServiceProvider) {
        this.accountsServiceProvider = accountsServiceProvider;
        return this;
    }

    CongestionChargeSystem build() {
        return new CongestionChargeSystem(chargeMethod, operationsTeam, eventLog, accountsServiceProvider);
    }
}