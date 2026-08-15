package com.workforceos.attendance.adapter.outbound.persistence.readmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Read-only projection of the scheduling module's {@code schedule_entry} table. */
@Entity
@Immutable
@Table(name = "schedule_entry")
public class ScheduleEntryReadEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "business_date")
    private LocalDate businessDate;

    @Column(name = "zone_id")
    private String zoneId;

    @Column(name = "planned_start")
    private Instant plannedStart;

    @Column(name = "planned_end")
    private Instant plannedEnd;

    protected ScheduleEntryReadEntity() {
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
}
