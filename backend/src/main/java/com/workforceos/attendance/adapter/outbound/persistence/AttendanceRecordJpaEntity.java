package com.workforceos.attendance.adapter.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/** JPA mapping of the derived attendance record. */
@Entity
@Table(name = "attendance_record")
public class AttendanceRecordJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @Column(name = "business_date", nullable = false)
    private LocalDate businessDate;

    @Column(name = "schedule_entry_id")
    private UUID scheduleEntryId;

    @Column(name = "policy_version_id")
    private UUID policyVersionId;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "regular_minutes", nullable = false)
    private long regularMinutes;

    @Column(name = "overtime_minutes", nullable = false)
    private long overtimeMinutes;

    @Column(name = "break_minutes", nullable = false)
    private long breakMinutes;

    @Column(name = "version", nullable = false)
    private long version;

    protected AttendanceRecordJpaEntity() {
    }

    public AttendanceRecordJpaEntity(UUID id, UUID tenantId, UUID employeeId, LocalDate businessDate,
                                     UUID scheduleEntryId, UUID policyVersionId, String status,
                                     long regularMinutes, long overtimeMinutes, long breakMinutes, long version) {
        this.id = id;
        this.tenantId = tenantId;
        this.employeeId = employeeId;
        this.businessDate = businessDate;
        this.scheduleEntryId = scheduleEntryId;
        this.policyVersionId = policyVersionId;
        this.status = status;
        this.regularMinutes = regularMinutes;
        this.overtimeMinutes = overtimeMinutes;
        this.breakMinutes = breakMinutes;
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

    public UUID getScheduleEntryId() {
        return scheduleEntryId;
    }

    public UUID getPolicyVersionId() {
        return policyVersionId;
    }

    public String getStatus() {
        return status;
    }

    public long getRegularMinutes() {
        return regularMinutes;
    }

    public long getOvertimeMinutes() {
        return overtimeMinutes;
    }

    public long getBreakMinutes() {
        return breakMinutes;
    }

    public long getVersion() {
        return version;
    }
}
