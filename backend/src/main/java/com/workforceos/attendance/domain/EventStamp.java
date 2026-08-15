package com.workforceos.attendance.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * A single ordered clock occurrence fed to an {@link EventPairingPolicy}.
 *
 * <p>Projected from immutable raw events at the application boundary so the pairing
 * strategy stays decoupled from persistence types.</p>
 */
public record EventStamp(Instant occurredAt, ClockEventKind kind) {

    public EventStamp {
        Objects.requireNonNull(occurredAt, "occurredAt");
        Objects.requireNonNull(kind, "kind");
    }
}
