package com.workforceos.attendance.domain;

import com.workforceos.shared.id.AttendanceRecordId;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.PolicyVersionId;
import com.workforceos.shared.id.ScheduleEntryId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;
import com.workforceos.shared.time.Minutes;

import java.util.Objects;

/**
 * A derived, recalculable attendance record for an employee on a business date.
 *
 * <p>Totals are derived from immutable raw events plus the effective policy/schedule and
 * can be recomputed with full traceability. Mutable aggregate updates are guarded by the
 * {@code version} field for optimistic locking.</p>
 */
public class AttendanceRecord {

    private final AttendanceRecordId id;
    private final TenantId tenantId;
    private final EmployeeId employeeId;
    private final BusinessDate businessDate;
    private final ScheduleEntryId scheduleEntryId;
    private final PolicyVersionId policyVersionId;
    private AttendanceStatus status;
    private Minutes regularMinutes;
    private Minutes overtimeMinutes;
    private Minutes breakMinutes;
    private long version;

    public AttendanceRecord(AttendanceRecordId id, TenantId tenantId, EmployeeId employeeId,
                            BusinessDate businessDate, ScheduleEntryId scheduleEntryId,
                            PolicyVersionId policyVersionId) {
        this(id, tenantId, employeeId, businessDate, scheduleEntryId, policyVersionId,
                AttendanceStatus.NORMAL, Minutes.ZERO, Minutes.ZERO, Minutes.ZERO, 0L);
    }

    public AttendanceRecord(AttendanceRecordId id, TenantId tenantId, EmployeeId employeeId,
                            BusinessDate businessDate, ScheduleEntryId scheduleEntryId,
                            PolicyVersionId policyVersionId, AttendanceStatus status,
                            Minutes regularMinutes, Minutes overtimeMinutes, Minutes breakMinutes,
                            long version) {
        this.id = Objects.requireNonNull(id, "id");
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.employeeId = Objects.requireNonNull(employeeId, "employeeId");
        this.businessDate = Objects.requireNonNull(businessDate, "businessDate");
        this.scheduleEntryId = scheduleEntryId;
        this.policyVersionId = policyVersionId;
        this.status = Objects.requireNonNull(status, "status");
        this.regularMinutes = Objects.requireNonNull(regularMinutes, "regularMinutes");
        this.overtimeMinutes = Objects.requireNonNull(overtimeMinutes, "overtimeMinutes");
        this.breakMinutes = Objects.requireNonNull(breakMinutes, "breakMinutes");
        this.version = version;
    }

    /** Recalculates derived totals, bumping the optimistic-lock version. */
    public void applyTotals(AttendanceStatus newStatus, Minutes regular, Minutes overtime, Minutes breaks) {
        this.status = Objects.requireNonNull(newStatus, "newStatus");
        this.regularMinutes = Objects.requireNonNull(regular, "regular");
        this.overtimeMinutes = Objects.requireNonNull(overtime, "overtime");
        this.breakMinutes = Objects.requireNonNull(breaks, "breaks");
        this.version++;
    }

    public AttendanceRecordId id() {
        return id;
    }

    public TenantId tenantId() {
        return tenantId;
    }

    public EmployeeId employeeId() {
        return employeeId;
    }

    public BusinessDate businessDate() {
        return businessDate;
    }

    public ScheduleEntryId scheduleEntryId() {
        return scheduleEntryId;
    }

    public PolicyVersionId policyVersionId() {
        return policyVersionId;
    }

    public AttendanceStatus status() {
        return status;
    }

    public Minutes regularMinutes() {
        return regularMinutes;
    }

    public Minutes overtimeMinutes() {
        return overtimeMinutes;
    }

    public Minutes breakMinutes() {
        return breakMinutes;
    }

    public long version() {
        return version;
    }
}
