package com.workforceos.shared.id;

import java.util.UUID;

/** Attendance policy identifier value object. */
public record PolicyId(UUID value) {

    public static PolicyId of(String value) {
        return new PolicyId(UUID.fromString(value));
    }

    public static PolicyId newId() {
        return new PolicyId(UUID.randomUUID());
    }
}
