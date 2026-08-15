package com.workforceos.attendance.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** JPA mapping of an actionable attendance exception. */
@Entity
@Table(name = "attendance_exception")
public class AttendanceExceptionJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "record_id", nullable = false)
    private UUID recordId;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "detail", nullable = false)
    private String detail;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected AttendanceExceptionJpaEntity() {
    }

    public AttendanceExceptionJpaEntity(UUID id, UUID tenantId, UUID recordId, String type, String severity,
                                        String state, String detail, Instant createdAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.recordId = recordId;
        this.type = type;
        this.severity = severity;
        this.state = state;
        this.detail = detail;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getRecordId() {
        return recordId;
    }

    public String getType() {
        return type;
    }

    public String getSeverity() {
        return severity;
    }

    public String getState() {
        return state;
    }

    public String getDetail() {
        return detail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
