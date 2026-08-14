package com.workforceos.iam.domain;

import com.workforceos.shared.id.OrgUnitId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.util.Objects;

/**
 * Binds a role to a user within an optional organizational scope.
 *
 * <p>A role grants capability; the organizational scope narrows which business objects
 * the subject may see. A null scope means tenant-wide.</p>
 */
public class RoleBinding {

    private final UserId userId;
    private final TenantId tenantId;
    private final Role role;
    private final OrgUnitId scopeOrgUnit;

    public RoleBinding(UserId userId, TenantId tenantId, Role role, OrgUnitId scopeOrgUnit) {
        this.userId = Objects.requireNonNull(userId, "userId");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.role = Objects.requireNonNull(role, "role");
        this.scopeOrgUnit = scopeOrgUnit;
    }

    public UserId userId() {
        return userId;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public Role role() {
        return role;
    }

    public OrgUnitId scopeOrgUnit() {
        return scopeOrgUnit;
    }
}
