package com.workforceos.shared.id;

import java.util.UUID;

/** Work location identifier value object. */
public record WorkLocationId(UUID value) {

    public static WorkLocationId of(String value) {
        return new WorkLocationId(UUID.fromString(value));
    }

    public static WorkLocationId newId() {
        return new WorkLocationId(UUID.randomUUID());
    }
}
