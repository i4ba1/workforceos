package com.workforceos.shared.id;

import java.util.UUID;

/** Tenant identifier value object. */
public record TenantId(UUID value) {

    public static TenantId of(String value) {
        return new TenantId(UUID.fromString(value));
    }

    public static TenantId newId() {
        return new TenantId(UUID.randomUUID());
    }
}
