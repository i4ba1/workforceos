package com.workforceos.attendance.domain;

import com.workforceos.shared.time.Minutes;

/**
 * Effective attendance policy parameters consumed by the rule strategies.
 *
 * <p>For Phase 3 a single default policy is used; the policy module will supply
 * effective-dated versions in a later phase.</p>
 */
public record AttendancePolicyParameters(
        Minutes graceMinutes,
        Minutes earlyLeaveThresholdMinutes,
        Minutes dailyOvertimeThresholdMinutes,
        Minutes minimumBreakMinutes,
        Minutes breakAfterMinutes,
        boolean breakPaid) {

    public AttendancePolicyParameters {
        if (graceMinutes == null || earlyLeaveThresholdMinutes == null
                || dailyOvertimeThresholdMinutes == null || minimumBreakMinutes == null
                || breakAfterMinutes == null) {
            throw new IllegalArgumentException("policy parameters must not be null");
        }
    }

    public static AttendancePolicyParameters defaults() {
        return new AttendancePolicyParameters(
                Minutes.of(10),
                Minutes.of(15),
                Minutes.of(480),
                Minutes.of(30),
                Minutes.ZERO,
                false);
    }
}
