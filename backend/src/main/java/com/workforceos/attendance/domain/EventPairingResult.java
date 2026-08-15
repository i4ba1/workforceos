package com.workforceos.attendance.domain;

import com.workforceos.shared.time.WorkInterval;

import java.util.List;

/**
 * The result of pairing ordered clock events into intervals.
 *
 * @param workedIntervals closed work intervals derived from the events
 * @param breakIntervals  closed break intervals
 * @param missingClockIn  true when no clock-in occurrence was present
 * @param missingClockOut true when a work interval was left open (no closing occurrence)
 */
public record EventPairingResult(
        List<WorkInterval> workedIntervals,
        List<WorkInterval> breakIntervals,
        boolean missingClockIn,
        boolean missingClockOut) {
}
