package com.workforceos.scheduling.domain;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.ShiftTemplateId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.time.BusinessDate;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Read-side port for schedules and shift templates. */
public interface ScheduleReader {

    Optional<ShiftTemplate> findShiftTemplate(TenantId tenantId, ShiftTemplateId id);

    List<ShiftTemplate> findShiftTemplates(TenantId tenantId);

    List<ScheduleEntry> findEntries(TenantId tenantId, EmployeeId employeeId, BusinessDate from, BusinessDate to);

    List<ScheduleEntry> findOverlapping(TenantId tenantId, EmployeeId employeeId, Instant start, Instant end);
}
