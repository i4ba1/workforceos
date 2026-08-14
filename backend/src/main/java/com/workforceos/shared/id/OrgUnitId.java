package com.workforceos.shared.id;

import java.util.UUID;

/** Organization unit identifier value object. */
public record OrgUnitId(UUID value) {

    public static OrgUnitId of(String value) {
        return new OrgUnitId(UUID.fromString(value));
    }

    public static OrgUnitId newId() {
        return new OrgUnitId(UUID.randomUUID());
    }
}
