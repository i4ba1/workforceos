package com.workforceos.shared.id;

import java.util.UUID;

/** Raw time event identifier value object. */
public record TimeEventId(UUID value) {

    public static TimeEventId of(String value) {
        return new TimeEventId(UUID.fromString(value));
    }

    public static TimeEventId newId() {
        return new TimeEventId(UUID.randomUUID());
    }
}
