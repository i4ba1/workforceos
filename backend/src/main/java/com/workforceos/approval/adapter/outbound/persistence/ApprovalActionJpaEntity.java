package com.workforceos.approval.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** JPA mapping of an immutable approval decision action. */
@Entity
@Table(name = "approval_action")
public class ApprovalActionJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "case_id", nullable = false)
    private UUID caseId;

    @Column(name = "actor_id", nullable = false)
    private UUID actorId;

    @Column(name = "decision", nullable = false)
    private String decision;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "acted_at", nullable = false)
    private Instant actedAt;

    @Column(name = "case_version", nullable = false)
    private long caseVersion;

    protected ApprovalActionJpaEntity() {
    }

    public ApprovalActionJpaEntity(UUID id, UUID tenantId, UUID caseId, UUID actorId, String decision,
                                   String reason, Instant actedAt, long caseVersion) {
        this.id = id;
        this.tenantId = tenantId;
        this.caseId = caseId;
        this.actorId = actorId;
        this.decision = decision;
        this.reason = reason;
        this.actedAt = actedAt;
        this.caseVersion = caseVersion;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public UUID getActorId() {
        return actorId;
    }

    public String getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

    public Instant getActedAt() {
        return actedAt;
    }

    public long getCaseVersion() {
        return caseVersion;
    }
}
