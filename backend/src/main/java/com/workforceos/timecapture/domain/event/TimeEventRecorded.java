package com.workforceos.timecapture.domain.event;

import com.workforceos.shared.id.EmployeeId;
import com.workforceos.shared.id.TenantId;
import com.workforceos.shared.id.TimeEventId;
import com.workforceos.timecapture.domain.TimeEventType;

import java.time.Instant;
import java.time.ZoneId;

/**
 * Published once a new raw time event has been durably committed.
 *
 * <p>Consumers: attendance recalculation and audit/read projections.</p>
 */
public record TimeEventRecorded(
        TenantId tenantId,
        EmployeeId employeeId,
        TimeEventId timeEventId,
        TimeEventType type,
        Instant occurredAt,
        ZoneId zoneId) {
}
