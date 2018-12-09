package com.trafficmon;

class CongestionChargeSystemBuilder {
    private PenaltiesService operationsTeam = OperationsTeam.getInstance();
    private EventLog eventLog = new StandardEventLog();
    private AccountsServiceProvider accountsServiceProvider = new AccountsServiceProviderAdapter(RegisteredCustomerAccountsService.getInstance());
    private ChargeMethod chargeMethod = new RevisedChargeMethod();

    private CongestionChargeSystemBuilder() {};

    public static CongestionChargeSystemBuilder aCongestionChargeSystem() {
        return new CongestionChargeSystemBuilder();
    }

    public CongestionChargeSystemBuilder withChargeSystem(ChargeMethod chargeMethod){
        this.chargeMethod = chargeMethod;
        return this;
    }

    public CongestionChargeSystemBuilder withOperationsTeam(PenaltiesService operationsTeam) {
        this.operationsTeam = operationsTeam;
        return this;
    }

    public CongestionChargeSystemBuilder withEventLog(EventLog eventLog) {
        this.eventLog = eventLog;
        return this;
    }

    public CongestionChargeSystemBuilder withAccountsServiceProvider(AccountsServiceProvider accountsServiceProvider) {
        this.accountsServiceProvider = accountsServiceProvider;
        return this;
    }

    public CongestionChargeSystem build() {
        return new CongestionChargeSystem(chargeMethod, operationsTeam, eventLog, accountsServiceProvider);
    }
}