package com.workforceos.attendance.domain.rule;

import com.workforceos.attendance.domain.AttendanceContext;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRule;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionType;

import java.util.List;

/** Fires MISSING_CLOCK_IN when events exist but no clock-in was recorded. */
public class MissingClockInRule implements AttendanceRule {

    @Override
    public List<ExceptionFinding> evaluate(AttendanceContext context, AttendancePolicyParameters policy) {
        if (context.hasAnyEvent() && context.missingClockIn()) {
            return List.of(new ExceptionFinding(ExceptionType.MISSING_CLOCK_IN, ExceptionSeverity.HIGH,
                    "No clock-in event recorded"));
        }
        return List.of();
    }
}
