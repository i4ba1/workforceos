package com.workforceos.organization.domain;

import com.workforceos.shared.id.LegalEntityId;
import com.workforceos.shared.id.TenantId;

import java.time.ZoneId;
import java.util.Objects;

/** A legal entity within a tenant, with a primary operating time zone. */
public class LegalEntity {

    private final LegalEntityId id;
    private final TenantId tenantId;
    private final String name;
    private final ZoneId primaryZone;

    public LegalEntity(LegalEntityId id, TenantId tenantId, String name, ZoneId primaryZone) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.name = Objects.requireNonNull(name, "name");
        this.primaryZone = Objects.requireNonNull(primaryZone, "primaryZone");
    }

    public LegalEntityId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }

    public ZoneId primaryZone() {
        return primaryZone;
    }
}
