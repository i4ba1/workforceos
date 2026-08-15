package com.workforceos.attendance.domain.rule;

import com.workforceos.attendance.domain.AttendanceContext;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRule;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionType;

import java.util.List;

/** Fires UNSCHEDULED_WORK when time events exist without a qualifying schedule. */
public class UnscheduledWorkRule implements AttendanceRule {

    @Override
    public List<ExceptionFinding> evaluate(AttendanceContext context, AttendancePolicyParameters policy) {
        if (!context.isScheduled() && context.hasAnyEvent()) {
            return List.of(new ExceptionFinding(ExceptionType.UNSCHEDULED_WORK, ExceptionSeverity.MEDIUM,
                    "Time events recorded without a qualifying schedule"));
        }
        return List.of();
    }
}
