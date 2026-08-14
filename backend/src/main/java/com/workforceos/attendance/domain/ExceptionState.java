package com.workforceos.attendance.domain;

/** Resolution state of an actionable attendance exception. */
public enum ExceptionState {
    OPEN,
    UNDER_REVIEW,
    RESOLVED,
    DISMISSED
}
