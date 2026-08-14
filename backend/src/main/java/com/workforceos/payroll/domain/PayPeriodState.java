package com.workforceos.payroll.domain;

/** State machine of a pay period: OPEN -> VALIDATING -> CLOSED -> REOPENED. */
public enum PayPeriodState {
    OPEN,
    VALIDATING,
    CLOSED,
    REOPENED
}
