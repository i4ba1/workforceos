package com.workforceos.attendance.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Read model of a raw time event projected from the time-capture table.
 */
public record ClockEventReadModel(Instant occurredAt, ClockEventKind kind, ZoneId zoneId) {

    public ClockEventReadModel {
        Objects.requireNonNull(occurredAt, "occurredAt");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(zoneId, "zoneId");
    }
}
