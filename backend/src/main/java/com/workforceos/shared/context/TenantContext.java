package com.workforceos.shared.context;

import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.util.Objects;
import java.util.Set;

/**
 * The authenticated subject and tenant boundary for the current request.
 *
 * <p>Populated per-request by the inbound web layer from the authenticated identity. All
 * tenant-owned reads/writes derive their tenant scope from this context; tenant IDs are
 * never trusted from a client-supplied value without matching the authenticated claim.</p>
 */
public record TenantContext(TenantId tenantId, UserId userId, Set<String> roles) {

    public TenantContext {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(userId, "userId");
        roles = roles == null ? Set.of() : Set.copyOf(roles);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }
}
