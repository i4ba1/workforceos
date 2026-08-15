package com.workforceos.attendance.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

/**
 * Read-side source of the facts the calculation needs: the planned shift, raw events, and
 * the tenant default zone. Implemented by a read-model adapter in the attendance module.
 */
public interface AttendanceDataSource {

    Optional<PlannedShift> findShift(TenantId tenantId, EmployeeId employeeId, BusinessDate businessDate);

    List<ClockEventReadModel> findEvents(TenantId tenantId, EmployeeId employeeId, Instant from, Instant to);

    ZoneId tenantDefaultZone(TenantId tenantId);
}
