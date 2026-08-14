package com.workforceos.policy.domain;

import com.workforceos.shared.id.PolicyId;
import com.workforceos.shared.id.TenantId;

import java.util.Objects;

/** An attendance policy root; actual rules live in versioned {@link PolicyVersion}s. */
public class AttendancePolicy {

    private final PolicyId id;
    private final TenantId tenantId;
    private final String name;

    public AttendancePolicy(PolicyId id, TenantId tenantId, String name) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.name = Objects.requireNonNull(name, "name");
    }

    public PolicyId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String name() {
        return name;
    }
}
