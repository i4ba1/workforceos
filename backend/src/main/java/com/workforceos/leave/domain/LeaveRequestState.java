package com.workforceos.leave.domain;

/** Lifecycle state of a leave request. */
public enum LeaveRequestState {
    REQUESTED,
    APPROVED,
    REJECTED,
    CANCELLED
}
