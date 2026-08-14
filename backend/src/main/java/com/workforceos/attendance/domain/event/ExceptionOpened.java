package com.workforceos.attendance.domain.event;

import com.workforceos.attendance.domain.ExceptionSeverity;
import com.workforceos.attendance.domain.ExceptionType;
import com.workforceos.shared.id.AttendanceRecordId;
import com.workforceos.shared.id.TenantId;

/**
 * Published when an actionable rule finding creates a new exception.
 *
 * <p>Consumers: manager queue and notification.</p>
 */
public record ExceptionOpened(
        TenantId tenantId,
        AttendanceRecordId attendanceRecordId,
        ExceptionType type,
        ExceptionSeverity severity) {
}
