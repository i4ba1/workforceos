package com.workforceos.shared.id;

import java.util.UUID;

/** Schedule entry identifier value object. */
public record ScheduleEntryId(UUID value) {

    public static ScheduleEntryId of(String value) {
        return new ScheduleEntryId(UUID.fromString(value));
    }

    public static ScheduleEntryId newId() {
        return new ScheduleEntryId(UUID.randomUUID());
    }
}
