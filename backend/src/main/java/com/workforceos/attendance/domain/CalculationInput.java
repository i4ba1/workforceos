package com.workforceos.attendance.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.time.BusinessDate;

import java.util.List;

/**
 * Input to the attendance calculation: the employee, business date, planned shift (if any),
 * ordered raw events, and the effective policy.
 */
public record CalculationInput(
        EmployeeId employeeId,
        BusinessDate businessDate,
        PlannedShift shift,
        List<EventStamp> events,
        AttendancePolicyParameters policy,
        boolean hasApprovedLeave,
        boolean isHoliday,
        boolean isRestDay) {
}
