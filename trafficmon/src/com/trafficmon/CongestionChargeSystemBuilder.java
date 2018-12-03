package com.trafficmon;

import java.util.ArrayList;
import java.util.List;

public class CongestionChargeSystemBuilder {
    private PenaltiesService operationsTeam = OperationsTeam.getInstance();
    private List<ZoneBoundaryCrossing> eventLog = new ArrayList<ZoneBoundaryCrossing>();
    private AccountsService accountsService = RegisteredCustomerAccountsService.getInstance();

    private CongestionChargeSystemBuilder() {};

    public static CongestionChargeSystemBuilder aCongestionChargeSystem() {
        return new CongestionChargeSystemBuilder();
    }

    public CongestionChargeSystemBuilder withOperationsTeam(PenaltiesService operationsTeam) {
        this.operationsTeam = operationsTeam;
        return this;
    }

    public CongestionChargeSystemBuilder withEventLog(List<ZoneBoundaryCrossing> eventLog) {
        this.eventLog = eventLog;
        return this;
    }

    public CongestionChargeSystemBuilder withAccountsService(AccountsService accountsService) {
        this.accountsService = accountsService;
        return this;
    }

    public CongestionChargeSystem build() {
        return new CongestionChargeSystem(operationsTeam, eventLog, accountsService);
    }
}