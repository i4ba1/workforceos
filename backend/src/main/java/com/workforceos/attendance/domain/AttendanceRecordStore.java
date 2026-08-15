package com.workforceos.attendance.domain;

import com.workforceos.shared.id.AttendanceRecordId;
import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;

import java.util.List;
import java.util.Optional;

/** Persistence port for derived attendance records and their exceptions. */
public interface AttendanceRecordStore {

    Optional<AttendanceRecord> find(TenantId tenantId, EmployeeId employeeId, BusinessDate businessDate);

    Optional<AttendanceRecord> findById(TenantId tenantId, AttendanceRecordId id);

    List<AttendanceRecord> findByTenant(TenantId tenantId);

    AttendanceRecord save(AttendanceRecord record);

    List<AttendanceException> findExceptions(TenantId tenantId, AttendanceRecordId recordId);

    void replaceExceptions(TenantId tenantId, AttendanceRecordId recordId, List<ExceptionFinding> findings);
}
