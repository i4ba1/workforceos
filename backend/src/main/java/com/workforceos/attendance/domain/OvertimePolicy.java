package com.workforceos.attendance.domain;

import com.workforceos.shared.time.Minutes;

/**
 * Converts eligible worked minutes into regular/overtime buckets based on a threshold.
 *
 * <p>Daily/weekly thresholds and approval requirements are configurable per policy; this
 * contract isolates that variation from the attendance calculator.</p>
 */
public interface OvertimePolicy {

    OvertimeBuckets bucket(Minutes workedMinutes, Minutes thresholdMinutes);
}
