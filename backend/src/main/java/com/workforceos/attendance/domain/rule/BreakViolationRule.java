package com.workforceos.attendance.domain.rule;

import com.workforceos.attendance.domain.AttendanceContext;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRule;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionType;

import java.util.List;

/** Fires BREAK_VIOLATION when a long work span lacks the required break. */
public class BreakViolationRule implements AttendanceRule {

    @Override
    public List<ExceptionFinding> evaluate(AttendanceContext context, AttendancePolicyParameters policy) {
        boolean breakRequired = policy.breakAfterMinutes().value() > 0;
        boolean workedPastThreshold = context.workedMinutes().compareTo(policy.breakAfterMinutes()) > 0;
        boolean breakInsufficient = context.breakMinutes().compareTo(policy.minimumBreakMinutes()) < 0;
        if (breakRequired && workedPastThreshold && breakInsufficient) {
            return List.of(new ExceptionFinding(ExceptionType.BREAK_VIOLATION, ExceptionSeverity.LOW,
                    "Required break not taken"));
        }
        return List.of();
    }
}
