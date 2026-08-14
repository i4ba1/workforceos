package com.workforceos.shared.id;

import java.util.UUID;

/** Employee identifier value object. */
public record EmployeeId(UUID value) {

    public static EmployeeId of(String value) {
        return new EmployeeId(UUID.fromString(value));
    }

    public static EmployeeId newId() {
        return new EmployeeId(UUID.randomUUID());
    }
}
