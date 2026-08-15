package com.workforceos.attendance.domain;

import com.workforceos.attendance.domain.rule.AbsenceRule;
import com.workforceos.attendance.domain.rule.BreakViolationRule;
import com.workforceos.attendance.domain.rule.EarlyLeaveRule;
import com.workforceos.attendance.domain.rule.LateArrivalRule;
import com.workforceos.attendance.domain.rule.MissingClockInRule;
import com.workforceos.attendance.domain.rule.MissingClockOutRule;
import com.workforceos.attendance.domain.rule.OvertimeRule;
import com.workforceos.attendance.domain.rule.UnscheduledWorkRule;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.Minutes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceCalculatorTest {

    private static final EmployeeId EMPLOYEE = EmployeeId.newId();
    private static final ZoneId JAKARTA = ZoneId.of("Asia/Jakarta");
    private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
    private static final BusinessDate DATE = BusinessDate.of(2026, 8, 14);

    private AttendanceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new AttendanceCalculator(
                new StandardEventPairingPolicy(),
                new SimpleOvertimePolicy(),
                List.of(
                        new LateArrivalRule(),
                        new EarlyLeaveRule(),
                        new MissingClockInRule(),
                        new MissingClockOutRule(),
                        new AbsenceRule(),
                        new OvertimeRule(),
                        new UnscheduledWorkRule(),
                        new BreakViolationRule()));
    }

    private static PlannedShift shift(ZoneId zone, Instant start, Instant end) {
        return new PlannedShift(ScheduleEntryId.newId(), zone, start, end, DATE);
    }

    private static EventStamp in(Instant at) {
        return new EventStamp(at, ClockEventKind.CLOCK_IN);
    }

    private static EventStamp out(Instant at) {
        return new EventStamp(at, ClockEventKind.CLOCK_OUT);
    }

    private CalculationResult calc(PlannedShift plannedShift, List<EventStamp> events, AttendancePolicyParameters policy) {
        return calculator.calculate(new CalculationInput(EMPLOYEE, DATE, plannedShift, events, policy, false, false, false));
    }

    private static List<ExceptionType> types(CalculationResult result) {
        return result.findings().stream().map(ExceptionFinding::type).toList();
    }

    @Test
    void normalDayShift_isNormalWithFullPayableMinutes() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z"); // 08:00 Jakarta
        Instant end = Instant.parse("2026-08-14T09:00:00Z");   // 16:00 Jakarta

        CalculationResult result = calc(shift(JAKARTA, start, end), List.of(in(start), out(end)),
                AttendancePolicyParameters.defaults());

        assertThat(result.status()).isEqualTo(AttendanceStatus.NORMAL);
        assertThat(result.workedMinutes().value()).isEqualTo(480);
        assertThat(result.regularMinutes().value()).isEqualTo(480);
        assertThat(result.overtimeMinutes().value()).isZero();
        assertThat(result.findings()).isEmpty();
    }

    @Test
    void crossMidnightShift_anchorsToBusinessDateAndComputesFullDuration() {
        Instant start = Instant.parse("2026-08-14T15:00:00Z"); // 22:00 Jakarta on business date
        Instant end = Instant.parse("2026-08-14T23:00:00Z");   // 06:00 Jakarta next day

        CalculationResult result = calc(shift(JAKARTA, start, end), List.of(in(start), out(end)),
                AttendancePolicyParameters.defaults());

        assertThat(result.status()).isEqualTo(AttendanceStatus.NORMAL);
        assertThat(result.workedMinutes().value()).isEqualTo(480);
    }

    @Test
    void dstSpringForward_doesNotCreatePhantomTime() {
        Instant start = ZonedDateTime.of(2026, 3, 8, 1, 0, 0, 0, NEW_YORK).toInstant(); // 06:00 UTC
        Instant end = ZonedDateTime.of(2026, 3, 8, 9, 0, 0, 0, NEW_YORK).toInstant();   // 13:00 UTC

        CalculationResult result = calc(shift(NEW_YORK, start, end), List.of(in(start), out(end)),
                AttendancePolicyParameters.defaults());

        assertThat(result.workedMinutes().value()).isEqualTo(420); // 7 elapsed hours, not 8
        assertThat(result.status()).isEqualTo(AttendanceStatus.NORMAL);
    }

    @Test
    void dstFallBack_countsRepeatedHourExactlyOnce() {
        Instant start = ZonedDateTime.of(2026, 11, 1, 1, 0, 0, 0, NEW_YORK).toInstant(); // 05:00 UTC (EDT)
        Instant end = ZonedDateTime.of(2026, 11, 1, 9, 0, 0, 0, NEW_YORK).toInstant();   // 14:00 UTC (EST)

        CalculationResult result = calc(shift(NEW_YORK, start, end), List.of(in(start), out(end)),
                AttendancePolicyParameters.defaults());

        assertThat(result.workedMinutes().value()).isEqualTo(540); // 9 elapsed hours
    }

    @Test
    void lateArrival_firesLate() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z");
        Instant end = Instant.parse("2026-08-14T09:00:00Z");
        Instant arrival = Instant.parse("2026-08-14T01:20:00Z"); // 20 minutes late

        CalculationResult result = calc(shift(JAKARTA, start, end), List.of(in(arrival), out(end)),
                AttendancePolicyParameters.defaults());

        assertThat(types(result)).contains(ExceptionType.LATE);
        assertThat(result.status()).isEqualTo(AttendanceStatus.LATE);
    }

    @Test
    void earlyLeave_firesEarlyLeave() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z");
        Instant end = Instant.parse("2026-08-14T09:00:00Z");
        Instant departure = Instant.parse("2026-08-14T08:00:00Z"); // 1 hour early

        CalculationResult result = calc(shift(JAKARTA, start, end), List.of(in(start), out(departure)),
                AttendancePolicyParameters.defaults());

        assertThat(types(result)).contains(ExceptionType.EARLY_LEAVE);
        assertThat(result.status()).isEqualTo(AttendanceStatus.EARLY_LEAVE);
    }

    @Test
    void missingClockOut_firesMissingPunchWithoutInventingFinalPunch() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z");

        CalculationResult result = calc(shift(JAKARTA, start, Instant.parse("2026-08-14T09:00:00Z")),
                List.of(in(start)), AttendancePolicyParameters.defaults());

        assertThat(types(result)).contains(ExceptionType.MISSING_CLOCK_OUT);
        assertThat(result.workedMinutes().value()).isZero();
        assertThat(result.status()).isEqualTo(AttendanceStatus.MISSING_CLOCK_OUT);
    }

    @Test
    void noEvents_firesAbsent() {
        CalculationResult result = calc(shift(JAKARTA, Instant.parse("2026-08-14T01:00:00Z"),
                Instant.parse("2026-08-14T09:00:00Z")), List.of(), AttendancePolicyParameters.defaults());

        assertThat(types(result)).contains(ExceptionType.ABSENT);
        assertThat(result.status()).isEqualTo(AttendanceStatus.ABSENT);
    }

    @Test
    void unscheduledWork_firesUnscheduledWork() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z");
        Instant end = Instant.parse("2026-08-14T09:00:00Z");

        CalculationResult result = calc(null, List.of(in(start), out(end)), AttendancePolicyParameters.defaults());

        assertThat(types(result)).contains(ExceptionType.UNSCHEDULED_WORK);
        assertThat(result.status()).isEqualTo(AttendanceStatus.UNSCHEDULED_WORK);
    }

    @Test
    void overtime_firesOvertimeAndSplitsBuckets() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z");
        Instant end = Instant.parse("2026-08-14T11:00:00Z"); // 10 hours worked

        CalculationResult result = calc(shift(JAKARTA, start, Instant.parse("2026-08-14T09:00:00Z")),
                List.of(in(start), out(end)), AttendancePolicyParameters.defaults());

        assertThat(types(result)).contains(ExceptionType.OVERTIME);
        assertThat(result.workedMinutes().value()).isEqualTo(600);
        assertThat(result.regularMinutes().value()).isEqualTo(480);
        assertThat(result.overtimeMinutes().value()).isEqualTo(120);
    }

    @Test
    void breakViolation_firesWhenRequiredBreakNotTaken() {
        Instant start = Instant.parse("2026-08-14T01:00:00Z");
        Instant breakStart = Instant.parse("2026-08-14T05:00:00Z");
        Instant breakEnd = Instant.parse("2026-08-14T05:10:00Z"); // only 10 minutes
        Instant end = Instant.parse("2026-08-14T11:00:00Z");

        AttendancePolicyParameters policy = new AttendancePolicyParameters(
                Minutes.of(10), Minutes.of(15), Minutes.of(720), Minutes.of(30), Minutes.of(240), false);

        CalculationResult result = calc(shift(JAKARTA, start, end),
                new ArrayList<>(List.of(
                        in(start),
                        new EventStamp(breakStart, ClockEventKind.BREAK_START),
                        new EventStamp(breakEnd, ClockEventKind.BREAK_END),
                        out(end))),
                policy);

        assertThat(types(result)).contains(ExceptionType.BREAK_VIOLATION);
        assertThat(result.breakMinutes().value()).isEqualTo(10);
    }
}
