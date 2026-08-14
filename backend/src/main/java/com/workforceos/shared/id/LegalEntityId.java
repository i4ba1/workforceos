package com.workforceos.shared.id;

import java.util.UUID;

/** Legal entity identifier value object. */
public record LegalEntityId(UUID value) {

    public static LegalEntityId of(String value) {
        return new LegalEntityId(UUID.fromString(value));
    }

    public static LegalEntityId newId() {
        return new LegalEntityId(UUID.randomUUID());
    }
}
