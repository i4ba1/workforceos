package com.workforceos.shared.id;

import java.util.UUID;

/** Attendance policy version identifier value object. */
public record PolicyVersionId(UUID value) {

    public static PolicyVersionId of(String value) {
        return new PolicyVersionId(UUID.fromString(value));
    }

    public static PolicyVersionId newId() {
        return new PolicyVersionId(UUID.randomUUID());
    }
}
