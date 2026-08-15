package com.workforceos.attendance.domain;

import java.util.List;

/**
 * Determines valid work and break intervals from ordered immutable events.
 *
 * <p>Implemented as a strategy because pairing semantics (break handling, missing-punch
 * tolerance, cross-midnight windowing) vary by tenant policy without changing the
 * calculation core.</p>
 */
public interface EventPairingPolicy {

    /**
     * @param orderedStamps clock occurrences ordered by {@link EventStamp#occurredAt()}
     * @return the paired intervals and missing-punch flags
     */
    EventPairingResult pair(List<EventStamp> orderedStamps);
}
