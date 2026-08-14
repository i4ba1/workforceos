package com.workforceos.approval.domain;

import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * An immutable record of a single approval decision on a case.
 *
 * <p>Every decision records actor, decision, reason, timestamp and the case version it
 * applied to.</p>
 */
public class ApprovalAction {

    private final ApprovalCaseId caseId;
    private final TenantId tenantId;
    private final UserId actorId;
    private final ApprovalDecision decision;
    private final String reason;
    private final Instant actedAt;
    private final long caseVersion;

    public ApprovalAction(ApprovalCaseId caseId, TenantId tenantId, UserId actorId,
                          ApprovalDecision decision, String reason, Instant actedAt, long caseVersion) {
        this.caseId = Objects.requireNonNull(caseId, "caseId");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.actorId = Objects.requireNonNull(actorId, "actorId");
        this.decision = Objects.requireNonNull(decision, "decision");
        this.reason = Objects.requireNonNull(reason, "reason");
        this.actedAt = Objects.requireNonNull(actedAt, "actedAt");
        this.caseVersion = caseVersion;
    }

    public ApprovalCaseId caseId() {
        return caseId;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public UserId actorId() {
        return actorId;
    }

    public ApprovalDecision decision() {
        return decision;
    }

    public String reason() {
        return reason;
    }

    public Instant actedAt() {
        return actedAt;
    }

    public long caseVersion() {
        return caseVersion;
    }
}
