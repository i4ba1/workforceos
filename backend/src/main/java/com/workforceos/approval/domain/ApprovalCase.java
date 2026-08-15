package com.workforceos.approval.domain;

import com.workforceos.shared.id.ApprovalCaseId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * A workflow decision case over a business subject (e.g. an attendance correction).
 *
 * <p>The subject is referenced generically ({@code subjectType} + {@code subjectId}) so
 * the approval module does not depend on the modules it approves. Updates are guarded by
 * {@code version} for optimistic locking: only one concurrent decision wins.</p>
 */
public class ApprovalCase {

    private final ApprovalCaseId id;
    private final TenantId tenantId;
    private final String subjectType;
    private final UUID subjectId;
    private final UserId openedBy;
    private final Instant openedAt;
    private final String reason;
    private ApprovalState state;
    private long version;

    public ApprovalCase(ApprovalCaseId id, TenantId tenantId, String subjectType, UUID subjectId,
                        UserId openedBy, Instant openedAt, String reason) {
        this(id, tenantId, subjectType, subjectId, openedBy, openedAt, reason, ApprovalState.OPEN, 0L);
    }

    public ApprovalCase(ApprovalCaseId id, TenantId tenantId, String subjectType, UUID subjectId,
                        UserId openedBy, Instant openedAt, String reason, ApprovalState state, long version) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.subjectType = Objects.requireNonNull(subjectType, "subjectType");
        this.subjectId = Objects.requireNonNull(subjectId, "subjectId");
        this.openedBy = Objects.requireNonNull(openedBy, "openedBy");
        this.openedAt = Objects.requireNonNull(openedAt, "openedAt");
        this.reason = reason;
        this.state = Objects.requireNonNull(state, "state");
        this.version = version;
    }

    /**
     * Applies a decision if the caller's expected version matches, otherwise signals a
     * conflict. Returns {@code false} without mutating when the versions differ.
     */
    public boolean decide(ApprovalDecision decision, long expectedVersion) {
        if (state != ApprovalState.OPEN) {
            throw new IllegalStateException("Approval case is not open");
        }
        if (expectedVersion != version) {
            return false;
        }
        this.state = switch (decision) {
            case APPROVE -> ApprovalState.APPROVED;
            case REJECT -> ApprovalState.REJECTED;
            case REQUEST_CORRECTION -> ApprovalState.OPEN;
        };
        this.version++;
        return true;
    }

    public ApprovalCaseId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public String subjectType() {
        return subjectType;
    }

    public UUID subjectId() {
        return subjectId;
    }

    public UserId openedBy() {
        return openedBy;
    }

    public Instant openedAt() {
        return openedAt;
    }

    public String reason() {
        return reason;
    }

    public ApprovalState state() {
        return state;
    }

    public long version() {
        return version;
    }
}
