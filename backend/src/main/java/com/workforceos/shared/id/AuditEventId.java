package com.workforceos.shared.id;

import java.util.UUID;

/** Audit event identifier value object. */
public record AuditEventId(UUID value) {

    public static AuditEventId of(String value) {
        return new AuditEventId(UUID.fromString(value));
    }

    public static AuditEventId newId() {
        return new AuditEventId(UUID.randomUUID());
    }
}
