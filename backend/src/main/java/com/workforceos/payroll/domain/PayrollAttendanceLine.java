package com.workforceos.payroll.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.time.Minutes;

/**
 * A read-model line of an employee's finalized attendance within a pay period.
 *
 * @param hasOpenException true when the employee has an unresolved exception blocking close
 */
public record PayrollAttendanceLine(
        EmployeeId employeeId,
        Minutes regularMinutes,
        Minutes overtimeMinutes,
        boolean hasOpenException) {
}
