package com.workforceos.leave.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.LeaveRequestId;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An approved absence window that suppresses expected attendance for its date range.
 *
 * @param paid whether the absence is payable time
 */
public record LeaveWindow(
        LeaveRequestId leaveRequestId,
        EmployeeId employeeId,
        LocalDate startDate,
        LocalDate endDate,
        boolean paid) {

    public LeaveWindow {
        Objects.requireNonNull(leaveRequestId, "leaveRequestId");
        Objects.requireNonNull(employeeId, "employeeId");
        Objects.requireNonNull(startDate, "startDate");
        Objects.requireNonNull(endDate, "endDate");
    }

    public boolean covers(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}
