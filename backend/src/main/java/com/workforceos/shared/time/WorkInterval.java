package com.workforceos.shared.time;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A closed worked interval between two instants, e.g. a paired clock-in/clock-out.
 *
 * <p>Elapsed duration is always derived from {@link Instant} arithmetic so that DST
 * transitions never create phantom worked time.</p>
 */
public record WorkInterval(Instant start, Instant end) {

    public WorkInterval {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("Work interval must end after it starts");
        }
    }

    public static WorkInterval of(Instant start, Instant end) {
        return new WorkInterval(start, end);
    }

    public Duration duration() {
        return Duration.between(start, end);
    }

    public Minutes durationInMinutes() {
        return Minutes.of(duration().toMinutes());
    }

    public boolean overlaps(WorkInterval other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    public boolean contains(Instant instant) {
        return !instant.isBefore(start) && instant.isBefore(end);
    }
}
