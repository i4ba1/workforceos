package com.workforceos.organization.domain;

import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.TenantId;

import java.util.Objects;

/** An organization unit participating in the reporting hierarchy. */
public class OrgUnit {

    private final OrgUnitId id;
    private final TenantId tenantId;
    private final String name;
    private final OrgUnitId parentId;

    public OrgUnit(OrgUnitId id, TenantId tenantId, String name, OrgUnitId parentId) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.name = Objects.requireNonNull(name, "name");
        this.parentId = parentId;
    }

    public OrgUnitId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }

    public OrgUnitId parentId() {
        return parentId;
    }
}
