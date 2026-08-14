package com.workforceos.organization.domain;

import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.WorkLocationId;

import java.time.ZoneId;
import java.util.Objects;

/** A physical work location with an explicit IANA time zone. */
public class WorkLocation {

    private final WorkLocationId id;
    private final TenantId tenantId;
    private final String name;
    private final ZoneId zoneId;

    public WorkLocation(WorkLocationId id, TenantId tenantId, String name, ZoneId zoneId) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.name = Objects.requireNonNull(name, "name");
        this.zoneId = Objects.requireNonNull(zoneId, "zoneId");
    }

    public WorkLocationId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }

    public ZoneId zoneId() {
        return zoneId;
    }
}
