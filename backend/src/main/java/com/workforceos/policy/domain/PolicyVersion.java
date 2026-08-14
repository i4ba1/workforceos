package com.workforceos.policy.domain;

import com.workforceos.shared.id.PolicyId;
import com.workforceos.shared.id.PolicyVersionId;
import com.workforceos.shared.id.TenantId;

import java.time.LocalDate;
import java.util.Objects;

/**
 * An effective-dated policy version.
 *
 * <p>Once published, a version is immutable. Future changes create a new version with a
 * new effective range; historical calculations remain attached to the version in effect
 * at that time.</p>
 */
public class PolicyVersion {

    private final PolicyVersionId id;
    private final PolicyId policyId;
    private final TenantId tenantId;
    private final int version;
    private final LocalDate effectiveFrom;
    private final LocalDate effectiveTo;
    private PolicyVersionState state;

    public PolicyVersion(PolicyVersionId id, PolicyId policyId, TenantId tenantId, int version,
                         LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.id = Objects.requireNonNull(id, "id");
        this.policyId = Objects.requireNonNull(policyId, "policyId");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.version = version;
        this.effectiveFrom = Objects.requireNonNull(effectiveFrom, "effectiveFrom");
        this.effectiveTo = effectiveTo;
        this.state = PolicyVersionState.DRAFT;
    }

    public void publish() {
        if (state == PolicyVersionState.PUBLISHED) {
            throw new IllegalStateException("Policy version already published");
        }
        this.state = PolicyVersionState.PUBLISHED;
    }

    public boolean effectiveOn(LocalDate date) {
        return !date.isBefore(effectiveFrom) && (effectiveTo == null || !date.isAfter(effectiveTo));
    }

    public PolicyVersionId id() {
        return id;
    }

    public PolicyId policyId() {
        return policyId;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public int version() {
        return version;
    }

    public LocalDate effectiveFrom() {
        return effectiveFrom;
    }

    public LocalDate effectiveTo() {
        return effectiveTo;
    }

    public PolicyVersionState state() {
        return state;
    }
}
