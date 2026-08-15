package com.workforceos.attendance.domain;

import com.workforceos.shared.time.Minutes;
import com.workforceos.shared.time.WorkInterval;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * The attendance calculation core.
 *
 * <p>Composes the pairing policy, overtime policy and rule strategies. This service has no
 * knowledge of persistence or the concrete rule set; rules are injected and composed,
 * honoring the open/closed principle.</p>
 */
public class AttendanceCalculator {

    private final EventPairingPolicy pairingPolicy;
    private final OvertimePolicy overtimePolicy;
    private final List<AttendanceRule> rules;

    public AttendanceCalculator(EventPairingPolicy pairingPolicy, OvertimePolicy overtimePolicy,
                                List<AttendanceRule> rules) {
        this.pairingPolicy = pairingPolicy;
        this.overtimePolicy = overtimePolicy;
        this.rules = List.copyOf(rules);
    }

    public CalculationResult calculate(CalculationInput input) {
        List<EventStamp> stamps = input.events().stream()
                .sorted(Comparator.comparing(EventStamp::occurredAt))
                .toList();
        EventPairingResult pairing = pairingPolicy.pair(stamps);

        Minutes worked = totalMinutes(pairing.workedIntervals());
        Minutes breaks = totalMinutes(pairing.breakIntervals());
        Minutes scheduled = input.shift() == null
                ? Minutes.ZERO
                : Minutes.of(Duration.between(input.shift().plannedStart(), input.shift().plannedEnd()).toMinutes());

        Instant firstArrival = pairing.workedIntervals().isEmpty()
                ? null : pairing.workedIntervals().get(0).start();
        Instant lastDeparture = pairing.workedIntervals().isEmpty()
                ? null : pairing.workedIntervals().get(pairing.workedIntervals().size() - 1).end();

        AttendanceContext context = new AttendanceContext(
                input.employeeId(),
                input.businessDate(),
                input.shift(),
                firstArrival,
                lastDeparture,
                worked,
                breaks,
                scheduled,
                !input.events().isEmpty(),
                pairing.missingClockIn(),
                pairing.missingClockOut(),
                input.hasApprovedLeave(),
                input.isHoliday(),
                input.isRestDay());

        List<ExceptionFinding> findings = rules.stream()
                .flatMap(rule -> rule.evaluate(context, input.policy()).stream())
                .toList();

        OvertimeBuckets buckets = overtimePolicy.bucket(worked, input.policy().dailyOvertimeThresholdMinutes());

        return new CalculationResult(deriveStatus(findings), worked, breaks,
                buckets.regular(), buckets.overtime(), findings);
    }

    private static Minutes totalMinutes(List<WorkInterval> intervals) {
        long seconds = intervals.stream().mapToLong(i -> i.duration().getSeconds()).sum();
        return Minutes.of(seconds / 60);
    }

    private static AttendanceStatus deriveStatus(List<ExceptionFinding> findings) {
        if (findings.isEmpty()) {
            return AttendanceStatus.NORMAL;
        }
        ExceptionFinding primary = findings.stream()
                .max(Comparator.comparingInt((ExceptionFinding f) -> f.severity().ordinal()))
                .orElseThrow();
        return AttendanceStatus.valueOf(primary.type().name());
    }
}
