package com.workforceos.attendance.domain;

import com.workforceos.shared.time.Minutes;

/**
 * Splits worked minutes into regular and overtime buckets at a single daily threshold.
 */
public class SimpleOvertimePolicy implements OvertimePolicy {

    @Override
    public OvertimeBuckets bucket(Minutes workedMinutes, Minutes thresholdMinutes) {
        if (workedMinutes.compareTo(thresholdMinutes) <= 0) {
            return new OvertimeBuckets(workedMinutes, Minutes.ZERO);
        }
        return new OvertimeBuckets(thresholdMinutes, workedMinutes.minus(thresholdMinutes));
    }
}
