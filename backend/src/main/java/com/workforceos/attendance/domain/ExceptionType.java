package com.workforceos.attendance.domain;

/**
 * Typed attendance exception vocabulary (Appendix A).
 *
 * <p>Each value corresponds to a rule finding that may require human decision. The
 * record-level {@link AttendanceStatus} may additionally be {@code NORMAL}.</p>
 */
public enum ExceptionType {
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
