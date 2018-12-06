package com.trafficmon;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.trafficmon.CongestionChargeSystemBuilder.aCongestionChargeSystem;

public class NewProjectTest {

    public static final int CHARGE_SEPERATION_TIME = LongStayOverMultipleEntryChargeSystem.CHARGE_SEPERATION_TIME;
    public static final int LONGEST_MINUTE_SPENT_IN_ZONE = LongStayOverMultipleEntryChargeSystem.LONGEST_MINUTE_SPENT_IN_ZONE;
    public static final int LONGEST_OUT_OF_ZONE_MINUTE = LongStayOverMultipleEntryChargeSystem.LONGEST_OUT_OF_ZONE_MINUTE;
    public static final BigDecimal MINIMUM_CHARGE = LongStayOverMultipleEntryChargeSystem.MINIMUM_CHARGE;
    public static final BigDecimal MEDIUM_CHARGE = LongStayOverMultipleEntryChargeSystem.MEDIUM_CHARGE;
    public static final BigDecimal MAXIMUM_CHARGE = LongStayOverMultipleEntryChargeSystem.MAXIMUM_CHARGE;

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    PenaltiesService operationsTeam = context.mock(PenaltiesService.class);
    AccountsServiceProvider accountsServiceProvider = context.mock(AccountsServiceProvider.class);
    private final Vehicle testVehicle = Vehicle.withRegistration("TOOKMESOLONG");
    private final Account TEST_ACCOUNT = new Account("test", testVehicle, new BigDecimal(10));
    private final List<ZoneBoundaryCrossing> testEventLog = new ArrayList<ZoneBoundaryCrossing>();
    private ChargePattern longStayOverMultipleEntryChargeSystem = new LongStayOverMultipleEntryChargeSystem();


    //not working, expecting 6 but getting 4
    @Test
    public void enterZoneBeforeSeparationTimeWithoutOverlapChargedCorrectAmount() throws AccountNotRegisteredException, InsufficientCreditException {
        context.checking(new Expectations(){{
            exactly(1).of(accountsServiceProvider).billVehicleAccount(testVehicle, BigDecimal.valueOf(6.00).setScale(2, RoundingMode.CEILING));
        }});

        DateTime entryTime = new DateTime(DateTimeZone.UTC)
                .withHourOfDay(9)
                .withMinuteOfHour(30);
        DateTime exitTime = entryTime.
                withHourOfDay(12)
                .withMinuteOfHour(30);
        testEventLog.add(new EntryEvent(testVehicle, entryTime));
        testEventLog.add(new ExitEvent(testVehicle, exitTime));
        CongestionChargeSystem congestionChargeSystem = aCongestionChargeSystem().withChargeSystem(longStayOverMultipleEntryChargeSystem).withOperationsTeam(operationsTeam).withEventLog(testEventLog).withAccountsServiceProvider(accountsServiceProvider).build();
        congestionChargeSystem.calculateCharges();

    }
}
