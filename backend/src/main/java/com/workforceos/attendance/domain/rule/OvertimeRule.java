package com.workforceos.attendance.domain.rule;

import com.workforceos.attendance.domain.AttendanceContext;
import com.workforceos.attendance.domain.AttendancePolicyParameters;
import com.workforceos.attendance.domain.AttendanceRule;
import com.workforceos.attendance.domain.ExceptionFinding;
import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionType;
import com.workforceos.shared.time.Minutes;

import java.util.List;

/** Fires OVERTIME when worked minutes exceed the daily threshold. */
public class OvertimeRule implements AttendanceRule {

    @Override
    public List<ExceptionFinding> evaluate(AttendanceContext context, AttendancePolicyParameters policy) {
        if (context.workedMinutes().compareTo(policy.dailyOvertimeThresholdMinutes()) > 0) {
            Minutes overtime = context.workedMinutes().minus(policy.dailyOvertimeThresholdMinutes());
            return List.of(new ExceptionFinding(ExceptionType.OVERTIME, ExceptionSeverity.MEDIUM,
                    "Overtime of " + overtime.value() + " minute(s) requires approval"));
        }
        return List.of();
    }
}
