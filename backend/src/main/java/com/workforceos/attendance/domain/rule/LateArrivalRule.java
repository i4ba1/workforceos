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

/** Fires LATE when first arrival exceeds scheduled start plus grace. */
public class LateArrivalRule implements AttendanceRule {

    @Override
    public List<ExceptionFinding> evaluate(AttendanceContext context, AttendancePolicyParameters policy) {
        if (!context.isScheduled() || context.firstArrival() == null) {
            return List.of();
        }
        Instant allowed = context.shift().plannedStart()
                .plus(policy.graceMinutes().value(), ChronoUnit.MINUTES);
        if (context.firstArrival().isAfter(allowed)) {
            long lateMinutes = Duration.between(allowed, context.firstArrival()).toMinutes();
            return List.of(new ExceptionFinding(ExceptionType.LATE, ExceptionSeverity.MEDIUM,
                    "Arrived " + lateMinutes + " minute(s) late"));
        }
        return List.of();
    }
}
