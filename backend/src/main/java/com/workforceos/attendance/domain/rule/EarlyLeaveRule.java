package com.workforceos.attendance.domain.rule;

import com.workforceos.attendance.domain.AttendanceContext;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRule;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionType;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Fires EARLY_LEAVE when the last departure precedes scheduled end beyond tolerance. */
public class EarlyLeaveRule implements AttendanceRule {

    @Override
    public List<ExceptionFinding> evaluate(AttendanceContext context, AttendancePolicyParameters policy) {
        if (!context.isScheduled() || context.lastDeparture() == null) {
            return List.of();
        }
        Instant allowed = context.shift().plannedEnd()
                .minus(policy.earlyLeaveThresholdMinutes().value(), ChronoUnit.MINUTES);
        if (context.lastDeparture().isBefore(allowed)) {
            long earlyMinutes = Duration.between(context.lastDeparture(), allowed).toMinutes();
            return List.of(new ExceptionFinding(ExceptionType.EARLY_LEAVE, ExceptionSeverity.MEDIUM,
                    "Left " + earlyMinutes + " minute(s) early"));
        }
        return List.of();
    }
}
