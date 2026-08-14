package com.workforceos.attendance.domain;

/**
 * Overall derived status of an attendance record.
 *
 * <p>Shares its vocabulary with {@link ExceptionType}: the record status reflects the
 * dominant condition (or {@link #NORMAL}), while exceptions carry the specific typed
 * findings that produced it.</p>
 */
public enum AttendanceStatus {
    NORMAL,
    LATE,
    EARLY_LEAVE,
    ABSENT,
    MISSING_CLOCK_IN,
    MISSING_CLOCK_OUT,
    OVERTIME,
    UNSCHEDULED_WORK,
    HOLIDAY_WORK,
    REST_DAY_WORK,
    BREAK_VIOLATION,
    OVERLAPPING_SHIFT
}
