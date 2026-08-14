package com.workforceos.shared.id;

import java.util.UUID;

/** Shift template identifier value object. */
public record ShiftTemplateId(UUID value) {

    public static ShiftTemplateId of(String value) {
        return new ShiftTemplateId(UUID.fromString(value));
    }

    public static ShiftTemplateId newId() {
        return new ShiftTemplateId(UUID.randomUUID());
    }
}
