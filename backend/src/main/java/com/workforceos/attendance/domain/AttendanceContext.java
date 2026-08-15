package com.workforceos.attendance.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.Minutes;

import java.time.Instant;

/**
 * Calculated attendance facts that rules evaluate.
 *
 * <p>Rules consume this context rather than reaching into repositories, keeping the rule
 * layer pure and deterministic.</p>
 */
public record AttendanceContext(
        EmployeeId employeeId,
        BusinessDate businessDate,
        PlannedShift shift,
        Instant firstArrival,
        Instant lastDeparture,
        Minutes workedMinutes,
        Minutes breakMinutes,
        Minutes scheduledMinutes,
        boolean hasAnyEvent,
        boolean missingClockIn,
        boolean missingClockOut,
        boolean hasApprovedLeave,
        boolean isHoliday,
        boolean isRestDay) {

    public boolean isScheduled() {
        return shift != null;
    }
}
