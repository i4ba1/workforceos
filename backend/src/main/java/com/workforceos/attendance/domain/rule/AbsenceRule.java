package com.workforceos.attendance.domain.rule;

import com.workforceos.attendance.domain.AttendanceContext;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRule;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionType;

import java.util.List;

/** Fires ABSENT when no qualifying time events exist and no leave is approved. */
public class AbsenceRule implements AttendanceRule {

    @Override
    public List<ExceptionFinding> evaluate(AttendanceContext context, AttendancePolicyParameters policy) {
        if (!context.hasAnyEvent() && !context.hasApprovedLeave()) {
            return List.of(new ExceptionFinding(ExceptionType.ABSENT, ExceptionSeverity.CRITICAL,
                    "No time events recorded and no approved leave"));
        }
        return List.of();
    }
}
