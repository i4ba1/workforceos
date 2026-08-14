package com.workforceos.attendance.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.Minutes;

/**
 * Calculated attendance facts that rules evaluate.
 *
 * <p>Rules consume this context rather than reaching into repositories, keeping the rule
 * layer pure and deterministic.</p>
 */
public record AttendanceContext(
        EmployeeId employeeId,
        BusinessDate businessDate,
        Minutes scheduledMinutes,
        Minutes workedMinutes,
        Minutes breakMinutes,
        boolean hasApprovedLeave,
        boolean isHoliday) {
}
