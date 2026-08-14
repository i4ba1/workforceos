package com.workforceos.attendance.domain.event;

import com.workforceos.shared.id.AttendanceRecordId;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;

/**
 * Published when a derived attendance record changes materially.
 *
 * <p>Consumers: exception workflow and reporting projections.</p>
 */
public record AttendanceRecalculated(
        TenantId tenantId,
        EmployeeId employeeId,
        AttendanceRecordId attendanceRecordId,
        BusinessDate businessDate) {
}
