package com.workforceos.timecapture.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** JPA mapping of the idempotency record for a time-event ingestion. */
@Entity
@Table(name = "ingestion_request")
public class IngestionRequestJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "time_event_id", nullable = false)
    private UUID timeEventId;

    @Column(name = "request_digest", nullable = false)
    private String requestDigest;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected IngestionRequestJpaEntity() {
    }

    public IngestionRequestJpaEntity(UUID id, UUID tenantId, String idempotencyKey, UUID timeEventId,
                                     String requestDigest, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.idempotencyKey = idempotencyKey;
        this.timeEventId = timeEventId;
        this.requestDigest = requestDigest;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public UUID getTimeEventId() {
        return timeEventId;
    }

    public String getRequestDigest() {
        return requestDigest;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
