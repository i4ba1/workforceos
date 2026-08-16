package com.workforceos.payroll.adapter.outbound.persistence.readmodel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.util.UUID;

/** Read-only projection of the attendance module's {@code attendance_record} table. */
@Entity
@Immutable
@Table(name = "attendance_record")
public class PayrollAttendanceRecordReadEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "tenant_id")
    private UUID tenantId;

    @Column(name = "employee_id")
    private UUID employeeId;

    @Column(name = "business_date")
    private LocalDate businessDate;

    @Column(name = "regular_minutes")
    private long regularMinutes;

    @Column(name = "overtime_minutes")
    private long overtimeMinutes;

    protected PayrollAttendanceRecordReadEntity() {
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

    public long getRegularMinutes() {
        return regularMinutes;
    }

    public long getOvertimeMinutes() {
        return overtimeMinutes;
    }
}
