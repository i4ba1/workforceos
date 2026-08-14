package com.workforceos.shared.id;

import java.util.UUID;

/** Notification identifier value object. */
public record NotificationId(UUID value) {

    public static NotificationId of(String value) {
        return new NotificationId(UUID.fromString(value));
    }

    public static NotificationId newId() {
        return new NotificationId(UUID.randomUUID());
    }
}
