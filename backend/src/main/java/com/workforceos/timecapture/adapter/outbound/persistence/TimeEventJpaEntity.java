package com.workforceos.timecapture.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** JPA mapping of the immutable raw time event. */
@Entity
@Table(name = "time_event")
public class TimeEventJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "zone_id", nullable = false)
    private String zoneId;

    @Column(name = "source", nullable = false)
    private String source;

    @Column(name = "source_event_id")
    private String sourceEventId;

    protected TimeEventJpaEntity() {
    }

    public TimeEventJpaEntity(UUID id, UUID tenantId, UUID employeeId, String eventType, Instant occurredAt,
                              Instant receivedAt, String zoneId, String source, String sourceEventId) {
        this.id = id;
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.receivedAt = receivedAt;
        this.zoneId = zoneId;
        this.source = source;
        this.sourceEventId = sourceEventId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getEmployeeId() {
        return employeeId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public String getZoneId() {
        return zoneId;
    }

    public String getSource() {
        return source;
    }

    public String getSourceEventId() {
        return sourceEventId;
    }
}
