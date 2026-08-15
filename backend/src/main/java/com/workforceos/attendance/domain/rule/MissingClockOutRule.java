package com.workforceos.attendance.domain.rule;

import com.workforceos.attendance.domain.AttendanceContext;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRule;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionType;

import java.util.List;

/** Fires MISSING_CLOCK_OUT when a work interval was left open. */
public class MissingClockOutRule implements AttendanceRule {

    @Override
    public List<ExceptionFinding> evaluate(AttendanceContext context, AttendancePolicyParameters policy) {
        if (context.missingClockOut()) {
            return List.of(new ExceptionFinding(ExceptionType.MISSING_CLOCK_OUT, ExceptionSeverity.HIGH,
                    "No clock-out event recorded"));
        }
        return List.of();
    }
}
