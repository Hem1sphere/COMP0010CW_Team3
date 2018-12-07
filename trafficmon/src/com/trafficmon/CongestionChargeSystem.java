package com.trafficmon;

import java.math.BigDecimal;
import java.util.*;

public class CongestionChargeSystem {

    public static final BigDecimal CHARGE_RATE_POUNDS_PER_MINUTE = BigDecimal.valueOf(0.05);

    //an event log that records all boundary crossing events
    private final EventLog eventLog; //extract as interface????
    private final PenaltiesService operationsTeam;
    private final AccountsServiceProvider accountsServiceProvider;
    private final ChargeMethod chargeMethod;

    public CongestionChargeSystem(ChargeMethod chargeMethod, PenaltiesService operationsTeam, EventLog eventLog, AccountsServiceProvider accountsServiceProvider) {
        this.operationsTeam = operationsTeam;
        this.eventLog = eventLog;
        this.accountsServiceProvider = accountsServiceProvider;
        this.chargeMethod = chargeMethod;
    }


    public void vehicleEnteringZone(Vehicle vehicle) {
        eventLog.logEntry(vehicle);
    }

    public void vehicleLeavingZone(Vehicle vehicle) {
        if (!eventLog.vehicleIsRegistered(vehicle)) {
            return;
        }
        eventLog.logExit(vehicle);
    }


    public void calculateCharges() {
        for (Map.Entry<Vehicle, List<ZoneBoundaryCrossing>> vehicleCrossings : eventLog.getCrossingsByVehicle().entrySet()) {
            Vehicle vehicle = vehicleCrossings.getKey();
            List<ZoneBoundaryCrossing> crossings = vehicleCrossings.getValue();

            if (!eventLog.orderingIsValid(crossings)) {
                operationsTeam.triggerInvestigationInto(vehicle);
            } else {

                BigDecimal charge = chargeMethod.calculateChargeForVehicle(crossings);

                try {
                    accountsServiceProvider.billVehicleAccount(vehicle, charge);
                } catch (InsufficientCreditException ice) {
                    operationsTeam.issuePenaltyNotice(vehicle, charge);
                } catch (AccountNotRegisteredException e) {
                    operationsTeam.issuePenaltyNotice(vehicle, charge);
                }
            }
        }
    }


}
