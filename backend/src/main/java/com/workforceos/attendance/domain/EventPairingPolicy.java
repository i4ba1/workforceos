package com.workforceos.attendance.domain;

import com.workforceos.shared.time.WorkInterval;

import java.util.List;

/**
 * Determines valid work intervals from ordered immutable events.
 *
 * <p>Implemented as a strategy because pairing semantics (break handling, missing-punch
 * tolerance, cross-midnight windowing) vary by tenant policy without changing the
 * calculation core.</p>
 */
public interface EventPairingPolicy {

    /**
     * @param orderedStamps clock occurrences ordered by {@link EventStamp#occurredAt()}
     * @return valid worked intervals derived from the stamps
     */
    List<WorkInterval> pair(List<EventStamp> orderedStamps);
}
