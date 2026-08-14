package com.workforceos.scheduling.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** JPA mapping of the schedule entry aggregate. */
@Entity
@Table(name = "schedule_entry")
public class ScheduleEntryJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "zone_id", nullable = false)
    private String zoneId;

    @Column(name = "planned_start", nullable = false)
    private Instant plannedStart;

    @Column(name = "planned_end", nullable = false)
    private Instant plannedEnd;

    @Column(name = "version", nullable = false)
    private long version;

    protected ScheduleEntryJpaEntity() {
    }

    public ScheduleEntryJpaEntity(UUID id, UUID tenantId, UUID employeeId, LocalDate businessDate, String zoneId,
                                  Instant plannedStart, Instant plannedEnd, long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.businessDate = businessDate;
        this.zoneId = zoneId;
        this.plannedStart = plannedStart;
        this.plannedEnd = plannedEnd;
        this.version = version;
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

    public LocalDate getBusinessDate() {
        return businessDate;
    }

    public String getZoneId() {
        return zoneId;
    }

    public Instant getPlannedStart() {
        return plannedStart;
    }

    public Instant getPlannedEnd() {
        return plannedEnd;
    }

    public long getVersion() {
        return version;
    }
}
