package com.workforceos.shared.id;

import java.util.UUID;

/** User account identifier value object. */
public record UserId(UUID value) {

    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId newId() {
        return new UserId(UUID.randomUUID());
    }
}
