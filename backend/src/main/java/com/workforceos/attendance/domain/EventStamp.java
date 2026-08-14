package com.workforceos.attendance.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * A single ordered clock occurrence fed to an {@link EventPairingPolicy}.
 *
 * <p>Projected from immutable raw events at the application boundary so the pairing
 * strategy stays decoupled from persistence types.</p>
 *
 * @param occurredAt the event instant
 * @param kind       whether the stamp opens or closes a worked window
 */
public record EventStamp(Instant occurredAt, Kind kind) {

    public enum Kind {
        START,
        END
    }

    public EventStamp {
        Objects.requireNonNull(occurredAt, "occurredAt");
        Objects.requireNonNull(kind, "kind");
    }
}
