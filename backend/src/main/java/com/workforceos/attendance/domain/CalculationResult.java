package com.workforceos.attendance.domain;

import com.workforceos.shared.time.Minutes;

import java.util.List;

/**
 * The derived outcome of an attendance calculation: status, minutes and rule findings.
 */
public record CalculationResult(
        AttendanceStatus status,
        Minutes workedMinutes,
        Minutes breakMinutes,
        Minutes regularMinutes,
        Minutes overtimeMinutes,
        List<ExceptionFinding> findings) {
}
