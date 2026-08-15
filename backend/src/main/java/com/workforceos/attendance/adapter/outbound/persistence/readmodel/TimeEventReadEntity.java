package com.workforceos.attendance.adapter.outbound.persistence.readmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.Instant;
import java.util.UUID;

/** Read-only projection of the time-capture module's {@code time_event} table. */
@Entity
@Immutable
@Table(name = "time_event")
public class TimeEventReadEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "occurred_at")
    private Instant occurredAt;

    @Column(name = "zone_id")
    private String zoneId;

    protected TimeEventReadEntity() {
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

    public String getZoneId() {
        return zoneId;
    }
}
