package com.workforceos.audit.domain;

import com.workforceos.shared.id.AuditEventId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * An immutable audit evidence record.
 *
 * <p>Append-only; carries the acting subject, action, target entity, a before/after digest
 * and a correlation ID. Security-relevant actions (role changes, policy publish, payroll
 * close/reopen, export, support access) are always captured.</p>
 */
public class AuditEvent {

    private final AuditEventId id;
    private final TenantId tenantId;
    private final UserId actorId;
    private final String action;
    private final String entityType;
    private final UUID entityId;
    private final String beforeDigest;
    private final String afterDigest;
    private final String correlationId;
    private final Instant occurredAt;

    public AuditEvent(AuditEventId id, TenantId tenantId, UserId actorId, String action,
                      String entityType, UUID entityId, String beforeDigest, String afterDigest,
                      String correlationId, Instant occurredAt) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.actorId = Objects.requireNonNull(actorId, "actorId");
        this.action = Objects.requireNonNull(action, "action");
        this.entityType = Objects.requireNonNull(entityType, "entityType");
        this.entityId = Objects.requireNonNull(entityId, "entityId");
        this.beforeDigest = beforeDigest;
        this.afterDigest = afterDigest;
        this.correlationId = Objects.requireNonNull(correlationId, "correlationId");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
    }

    public AuditEventId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public UserId actorId() {
        return actorId;
    }

    public String action() {
        return action;
    }

    public String entityType() {
        return entityType;
    }

    public UUID entityId() {
        return entityId;
    }

    public String beforeDigest() {
        return beforeDigest;
    }

    public String afterDigest() {
        return afterDigest;
    }

    public String correlationId() {
        return correlationId;
    }

    public Instant occurredAt() {
        return occurredAt;
    }
}
